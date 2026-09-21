package name.ncg777.maths.neural;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.concurrent.CancellationException;
import org.apache.commons.math3.random.MersenneTwister;
import name.ncg777.maths.Trit;
import name.ncg777.maths.neural.TernaryTrainingOptions.Mode;
import name.ncg777.maths.physics.TernarySpinModel;
import name.ncg777.maths.physics.TernarySpinModel.ExactDistribution;

/**
 * Full-batch learning for a fully visible ternary Boltzmann machine. Learns
 * every undirected coupling, field and neutrality penalty from complete states.
 * Exact mode follows the likelihood gradient; persistent Gibbs estimates its
 * model phase. There are no hidden units, missing values or training constraints.
 */
public final class TernaryBoltzmannTrainer {
  public static final int MAX_NODES = 64;
  public static final int MAX_EXAMPLES = 100_000;

  private TernaryBoltzmannTrainer() {}

  /** Scores are unregularized and higher is better. Epoch zero is the initial model. */
  public record Epoch(int epoch, double trainingScore, OptionalDouble validationScore) {}

  /**
   * With validation, model is the best scoring checkpoint (possibly epoch zero).
   * Otherwise it is the last model. History records evaluated models, including
   * later epochs that may have been discarded. Exact scores are mean joint log
   * likelihood; sampled-mode scores are mean per-site log pseudolikelihood.
   */
  public record Result(TernarySpinModel model, double beta, Mode mode,
      List<Epoch> history, int selectedEpoch, boolean earlyStopped, long estimatedWork) {
    public Result { history = List.copyOf(history); }
  }

  public static Result train(TernarySpinModel initial, int[][] examples, TernaryTrainingOptions options) {
    return train(initial, examples, null, options);
  }

  /** Validation rows never contribute to gradients or particle initialization. */
  public static Result train(TernarySpinModel initial, int[][] examples, int[][] validation,
      TernaryTrainingOptions options) {
    Objects.requireNonNull(initial);
    Objects.requireNonNull(options);
    int n = initial.size();
    if (n < 1 || n > MAX_NODES) throw new IllegalArgumentException("Training requires 1 to " + MAX_NODES + " nodes");
    validateRows(examples, n);
    if (validation != null) validateRows(validation, n);
    long work = estimateWork(n, examples.length, validation == null ? 0 : validation.length, options);
    if (work > options.maxWork()) {
      throw new IllegalArgumentException("Estimated training work " + work + " exceeds maxWork "
          + options.maxWork() + "; reduce epochs/data/model size or use sampled mode");
    }
    checkParameters(initial, options.maxAbsParameter());
    // Copy only after the resource checks. Callers retain ownership of their data.
    int[][] data = copy(examples);
    int[][] heldOut = validation == null ? null : copy(validation);
    Moments observed = moments(data, n);
    var random = new MersenneTwister(options.seed());
    int[][] particles = null;
    if (options.mode() == Mode.PERSISTENT_GIBBS) {
      particles = new int[options.particles()][];
      for (int p = 0; p < particles.length; p++) {
        checkCancelled();
        particles[p] = advance(initial, data[random.nextInt(data.length)], options.beta(), options.burnIn(), random);
      }
    }

    TernarySpinModel current = initial, best = initial;
    int bestEpoch = 0, stale = 0;
    double bestScore = Double.NEGATIVE_INFINITY, patienceScore = Double.NEGATIVE_INFINITY;
    boolean earlyStopped = false;
    var history = new ArrayList<Epoch>();
    for (int epoch = 0; epoch <= options.epochs(); epoch++) {
      checkCancelled();
      ExactDistribution exact = options.mode() == Mode.EXACT
          ? current.exactDistribution(options.beta(), s -> true, options.maxExactStates()) : null;
      double trainingScore = score(current, data, options.beta(), exact);
      OptionalDouble validationScore = heldOut == null ? OptionalDouble.empty()
          : OptionalDouble.of(score(current, heldOut, options.beta(), exact));
      history.add(new Epoch(epoch, trainingScore, validationScore));
      if (heldOut == null) {
        best = current;
        bestEpoch = epoch;
      } else {
        double value = validationScore.getAsDouble();
        if (value > bestScore) {
          best = current;
          bestEpoch = epoch;
          bestScore = value;
        }
        if (epoch == 0 || value > patienceScore + options.minImprovement()) {
          patienceScore = value;
          stale = 0;
        } else if (++stale >= options.patience()) {
          earlyStopped = epoch < options.epochs();
          break;
        }
      }
      if (epoch == options.epochs()) break;
      Moments predicted;
      if (exact != null) {
        predicted = new Moments(n);
        for (int k = 0; k < exact.size(); k++) {
          if ((k & 255) == 0) checkCancelled();
          predicted.add(exact.state(k), exact.distribution().probability(k));
        }
      } else {
        for (int p = 0; p < particles.length; p++) {
          checkCancelled();
          particles[p] = advance(current, particles[p], options.beta(), options.sweeps(), random);
        }
        predicted = moments(particles, n);
      }
      current = update(current, observed, predicted, options);
    }
    return new Result(best, options.beta(), options.mode(), history, bestEpoch, earlyStopped, work);
  }

  private static void validateRows(int[][] rows, int dimension) {
    Objects.requireNonNull(rows);
    if (rows.length == 0 || rows.length > MAX_EXAMPLES) {
      throw new IllegalArgumentException("Datasets must contain 1 to " + MAX_EXAMPLES + " examples");
    }
    for (int[] row : rows) {
      if (row == null || row.length != dimension) throw new IllegalArgumentException("Example dimension mismatch");
      for (int value : row) Trit.BUF(value);
    }
  }

  /** Conservative work units proportional to dense interaction visits, not milliseconds. */
  private static long estimateWork(int n, int trainingRows, int validationRows, TernaryTrainingOptions o) {
    long candidates = 1;
    if (o.mode() == Mode.EXACT) {
      for (int i = 0; i < n; i++) {
        if (candidates > o.maxExactStates() / 3) {
          throw new IllegalArgumentException("Exact training exceeds maxExactStates; explicitly select PERSISTENT_GIBBS");
        }
        candidates *= 3;
      }
    }
    try {
      long rows = (long) trainingRows + validationRows;
      long phase = o.mode() == Mode.EXACT ? 4 * candidates : 0;
      long evaluation = Math.multiplyExact(o.epochs() + 1L, Math.addExact(phase, 4 * rows));
      long sampling = o.mode() == Mode.EXACT ? 0 : Math.multiplyExact(o.particles(),
          Math.addExact(4L * o.burnIn(), Math.multiplyExact(o.epochs(), 4L * o.sweeps() + 1)));
      return Math.multiplyExact((long) n * n, Math.addExact(trainingRows, Math.addExact(evaluation, sampling)));
    } catch (ArithmeticException overflow) {
      throw new IllegalArgumentException("Training work estimate overflow", overflow);
    }
  }

  private static void checkParameters(TernarySpinModel model, double bound) {
    for (double[] row : model.couplings()) for (double value : row) checkBound(value, bound);
    for (double value : model.fields()) checkBound(value, bound);
    for (double value : model.penalties()) checkBound(value, bound);
  }

  private static void checkBound(double value, double bound) {
    if (Math.abs(value) > bound) throw new IllegalArgumentException("Initial parameter exceeds maxAbsParameter");
  }

  private static int[][] copy(int[][] rows) {
    int[][] result = new int[rows.length][];
    for (int i = 0; i < rows.length; i++) result[i] = rows[i].clone();
    return result;
  }

  private static Moments moments(int[][] rows, int n) {
    var result = new Moments(n);
    for (int[] row : rows) {
      checkCancelled();
      result.add(row, 1.0 / rows.length);
    }
    return result;
  }

  private static final class Moments {
    final double[] orientation, participation;
    final double[][] agreement;

    Moments(int n) {
      orientation = new double[n];
      participation = new double[n];
      agreement = new double[n][n];
    }

    void add(int[] state, double weight) {
      for (int i = 0; i < state.length; i++) {
        orientation[i] += weight * state[i];
        participation[i] += weight * state[i] * state[i];
        for (int j = i + 1; j < state.length; j++) agreement[i][j] += weight * state[i] * state[j];
      }
    }
  }

  private static TernarySpinModel update(TernarySpinModel model, Moments data, Moments predicted,
      TernaryTrainingOptions options) {
    double[][] j = model.couplings();
    double[] h = model.fields(), delta = model.penalties();
    for (int i = 0; i < model.size(); i++) {
      h[i] = step(h[i], data.orientation[i] - predicted.orientation[i], options);
      delta[i] = step(delta[i], predicted.participation[i] - data.participation[i], options);
      for (int k = i + 1; k < model.size(); k++) {
        j[i][k] = step(j[i][k], data.agreement[i][k] - predicted.agreement[i][k], options);
        j[k][i] = j[i][k];
      }
    }
    return new TernarySpinModel(j, delta, h);
  }

  private static double step(double value, double difference, TernaryTrainingOptions o) {
    double next = value + o.learningRate() * (o.beta() * difference - o.l2() * value);
    finite(next);
    return Math.max(-o.maxAbsParameter(), Math.min(o.maxAbsParameter(), next));
  }

  private static double score(TernarySpinModel model, int[][] rows, double beta, ExactDistribution exact) {
    double mean = 0;
    for (int[] row : rows) {
      checkCancelled();
      double value = 0;
      if (exact != null) {
        value = -beta * model.energy(row) - exact.distribution().logPartitionFunction();
      } else {
        for (int i = 0; i < model.size(); i++) {
          value += model.conditionalDistribution(row, i, beta).logProbability(row[i] + 1) / model.size();
        }
      }
      mean += finite(value) / rows.length;
    }
    return finite(mean);
  }

  private static double finite(double value) {
    if (!Double.isFinite(value)) {
      throw new ArithmeticException("Nonfinite training arithmetic; reduce beta, learning rate or parameter bounds");
    }
    return value;
  }

  private static void checkCancelled() {
    if (Thread.currentThread().isInterrupted()) throw new CancellationException("Training interrupted");
  }

  private static int[] advance(TernarySpinModel model, int[] initial, double beta, int sweeps,
      MersenneTwister random) {
    int[] state = initial.clone();
    for (int sweep = 0; sweep < sweeps; sweep++) {
      checkCancelled();
      state = model.gibbsSample(state, beta, 1, random);
    }
    return state;
  }
}
