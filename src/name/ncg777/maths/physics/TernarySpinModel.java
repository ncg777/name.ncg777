package name.ncg777.maths.physics;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import org.apache.commons.math3.random.RandomGenerator;
import name.ncg777.maths.Trit;
import name.ncg777.maths.enumerations.WordEnumeration;
import name.ncg777.statistics.BoltzmannDistribution;

/**
 * Immutable spin-one model: E(s) = -sum(i&lt;j) J[i][j] s[i] s[j]
 * + sum(i) delta[i] s[i]^2 - sum(i) h[i] s[i].
 * Positive J favors agreement, negative J opposition, positive delta neutrality.
 * This equilibrium model requires symmetric couplings and a zero diagonal.
 */
public final class TernarySpinModel {
  public static final int DEFAULT_MAX_STATES = 1_000_000;
  private final double[][] couplings;
  private final double[] penalties;
  private final double[] fields;

  public TernarySpinModel(double[][] couplings, double[] penalties, double[] fields) {
    int n = fields.length;
    if (penalties.length != n || couplings.length != n) {
      throw new IllegalArgumentException("Parameter dimensions must agree");
    }
    this.penalties = penalties.clone();
    this.fields = fields.clone();
    this.couplings = new double[n][];
    for (int i = 0; i < n; i++) {
      if (couplings[i].length != n) throw new IllegalArgumentException("Couplings must be square");
      this.couplings[i] = couplings[i].clone();
      requireFinite(penalties[i]);
      requireFinite(fields[i]);
      for (double value : couplings[i]) requireFinite(value);
    }
    for (int i = 0; i < n; i++) {
      if (couplings[i][i] != 0) throw new IllegalArgumentException("Diagonal must be zero");
      for (int j = 0; j < i; j++) {
        if (couplings[i][j] != couplings[j][i]) {
          throw new IllegalArgumentException("Couplings must be symmetric");
        }
      }
    }
  }

  private static double requireFinite(double value) {
    if (!Double.isFinite(value)) throw new IllegalArgumentException("Nonfinite parameter or energy");
    return value;
  }

  public int size() { return fields.length; }

  /** Defensive parameter copies for fitting or persisting a model. */
  public double[][] couplings() {
    double[][] copy = new double[size()][];
    for (int i = 0; i < size(); i++) copy[i] = couplings[i].clone();
    return copy;
  }

  public double[] penalties() { return penalties.clone(); }
  public double[] fields() { return fields.clone(); }

  private void validateState(int[] state) {
    if (state.length != size()) throw new IllegalArgumentException("State dimension mismatch");
    for (int value : state) Trit.BUF(value);
  }

  public double energy(int[] state) {
    validateState(state);
    double result = 0;
    for (int i = 0; i < size(); i++) {
      result += penalties[i] * state[i] * state[i] - fields[i] * state[i];
      for (int j = i + 1; j < size(); j++) result -= couplings[i][j] * state[i] * state[j];
    }
    return requireFinite(result);
  }

  public ExactDistribution exactDistribution(double beta) {
    return exactDistribution(beta, state -> true, DEFAULT_MAX_STATES);
  }

  /**
   * Enumerates all 3^n states, retaining only those accepted by a hard constraint.
   * maxStates bounds the unfiltered enumeration before allocation; constraints
   * do not make this algorithm sub-exponential. Callbacks receive state copies.
   */
  public ExactDistribution exactDistribution(double beta, Predicate<int[]> allowed, int maxStates) {
    Objects.requireNonNull(allowed);
    if (maxStates < 1) throw new IllegalArgumentException("maxStates must be positive");
    if (!Double.isFinite(beta) || beta < 0) throw new IllegalArgumentException("Invalid beta");
    int count = 1;
    for (int i = 0; i < size(); i++) {
      if (count > maxStates / 3) throw new IllegalArgumentException("Exact enumeration exceeds maxStates");
      count *= 3;
    }
    var states = new ArrayList<int[]>();
    var energies = new ArrayList<Double>();
    var words = new WordEnumeration(size(), 3);
    while (words.hasMoreElements()) {
      int[] state = words.nextElement();
      for (int i = 0; i < state.length; i++) state[i]--;
      if (allowed.test(state.clone())) {
        states.add(state);
        energies.add(energy(state));
      }
    }
    if (states.isEmpty()) throw new IllegalArgumentException("Constraint excludes every state");
    double[] energyArray = new double[energies.size()];
    for (int i = 0; i < energies.size(); i++) energyArray[i] = energies.get(i);
    return new ExactDistribution(size(), states.toArray(new int[0][]), energyArray, beta);
  }

  /**
   * Returns p(s[site] = -1, 0, +1 | other sites). Its expectation is a thermal
   * ternary activation, retaining participation separately from orientation.
   */
  public BoltzmannDistribution conditionalDistribution(int[] state, int site, double beta) {
    validateState(state);
    Objects.checkIndex(site, size());
    double localField = fields[site];
    for (int j = 0; j < size(); j++) localField += couplings[site][j] * state[j];
    requireFinite(localField);
    return new BoltzmannDistribution(new double[] {
        requireFinite(penalties[site] + localField), 0,
        requireFinite(penalties[site] - localField)}, beta);
  }

  /**
   * Systematic single-site Gibbs sweeps; returns a fresh state. Finite sweeps
   * produce an approximate equilibrium sample, with no mixing guarantee.
   * This samples the unconstrained model (use exactDistribution for hard constraints).
   */
  public int[] gibbsSample(int[] initial, double beta, int sweeps, RandomGenerator random) {
    validateState(initial);
    Objects.requireNonNull(random);
    if (sweeps < 0 || !Double.isFinite(beta) || beta < 0) {
      throw new IllegalArgumentException("Invalid sweep count or beta");
    }
    int[] state = initial.clone();
    for (int sweep = 0; sweep < sweeps; sweep++) {
      for (int site = 0; site < size(); site++) {
        state[site] = conditionalDistribution(state, site, beta).sample(random) - 1;
      }
    }
    return state;
  }

  /** Exact observables on the enumerated, possibly constrained state space. */
  public static final class ExactDistribution {
    private final int dimension;
    private final int[][] states;
    private final double[] energies;
    private final BoltzmannDistribution distribution;

    private ExactDistribution(int dimension, int[][] states, double[] energies, double beta) {
      this.dimension = dimension;
      this.states = states;
      this.energies = energies;
      distribution = new BoltzmannDistribution(energies, beta);
    }

    public int size() { return states.length; }
    public int[] state(int index) { return states[index].clone(); }
    public BoltzmannDistribution distribution() { return distribution; }
    public int[] sample(RandomGenerator random) { return state(distribution.sample(random)); }
    public double meanEnergy() { return distribution.expectation(energies); }

    public double expectation(ToDoubleFunction<int[]> observable) {
      double[] values = new double[size()];
      for (int i = 0; i < size(); i++) values[i] = observable.applyAsDouble(state(i));
      return distribution.expectation(values);
    }

    public double probability(Predicate<int[]> event) {
      return expectation(state -> event.test(state) ? 1 : 0);
    }

    public double meanOrientation(int site) {
      Objects.checkIndex(site, dimension);
      return expectation(state -> state[site]);
    }

    public double participation(int site) {
      Objects.checkIndex(site, dimension);
      return expectation(state -> state[site] * state[site]);
    }

    public double correlation(int first, int second) {
      Objects.checkIndex(first, dimension);
      Objects.checkIndex(second, dimension);
      return expectation(state -> state[first] * state[second]);
    }

    public double covariance(int first, int second) {
      return correlation(first, second) - meanOrientation(first) * meanOrientation(second);
    }
  }
}
