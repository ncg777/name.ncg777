package name.ncg777.maths.neural.apps;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Callable;
import org.apache.commons.math3.random.MersenneTwister;
import name.ncg777.maths.neural.TernaryBoltzmannTrainer;
import name.ncg777.maths.neural.TernaryTrainingOptions;
import name.ncg777.maths.neural.TernaryTrainingOptions.Mode;
import name.ncg777.maths.neural.TrainedTernaryNetwork;
import name.ncg777.maths.physics.TernarySpinModel;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** Learn a small signed network from reproducible synthetic observations. */
@Command(name = "ternary-training", mixinStandardHelpOptions = true,
    description = "Fit a three-node ternary network, validate it and optionally save its parameters.")
public class TernaryTrainingDemo implements Callable<Integer> {
  @Option(names = "--mode", defaultValue = "EXACT", description = "${COMPLETION-CANDIDATES}")
  private Mode mode;
  @Option(names = "--epochs", defaultValue = "200", description = "Maximum training epochs (default: 200).")
  private int epochs;
  @Option(names = "--seed", defaultValue = "777", description = "Reproducible data and training seed.")
  private long seed;
  @Option(names = "--output", description = "Optional JSON inference checkpoint.")
  private Path output;

  public static void main(String[] args) {
    System.exit(new CommandLine(new TernaryTrainingDemo()).execute(args));
  }

  @Override
  public Integer call() throws Exception {
    var options = TernaryTrainingOptions.builder().mode(mode).epochs(epochs).seed(seed).build();
    var source = new TernarySpinModel(new double[][] {{0, 1, -0.8}, {1, 0, 0}, {-0.8, 0, 0}},
        new double[] {0.3, 0.7, 0.4}, new double[] {0.3, -0.1, 0});
    var truth = source.exactDistribution(1);
    var random = new MersenneTwister(seed);
    int[][] training = new int[512][], validation = new int[128][];
    for (int i = 0; i < training.length; i++) training[i] = truth.sample(random);
    for (int i = 0; i < validation.length; i++) validation[i] = truth.sample(random);
    var initial = new TernarySpinModel(new double[3][3], new double[3], new double[3]);
    var result = TernaryBoltzmannTrainer.train(initial, training, validation, options);
    String metric = mode == Mode.EXACT ? "mean joint log-likelihood" : "mean per-site log-pseudolikelihood";
    var first = result.history().get(0);
    var selected = result.history().get(result.selectedEpoch());
    System.out.printf(Locale.ROOT, "%s; estimated work = %d%n", mode, result.estimatedWork());
    System.out.println("Metric: " + metric + " (higher is better)");
    System.out.printf(Locale.ROOT, "Training: %.6f -> %.6f; validation: %.6f -> %.6f%n",
        first.trainingScore(), selected.trainingScore(), first.validationScore().getAsDouble(),
        selected.validationScore().getAsDouble());
    System.out.printf(Locale.ROOT, "Selected epoch %d of %d completed; early stopped = %s%n",
        result.selectedEpoch(), result.history().size() - 1, result.earlyStopped());
    System.out.println("Fields: " + Arrays.toString(result.model().fields()));
    System.out.println("Neutrality penalties: " + Arrays.toString(result.model().penalties()));
    System.out.println("Connections: " + Arrays.deepToString(result.model().couplings()));
    System.out.println("P(node 1 = -1,0,+1 | nodes 0=+1,2=-1): " + Arrays.toString(
        result.model().conditionalDistribution(new int[] {1, 0, -1}, 1, result.beta()).probabilities()));
    System.out.println("Generated pattern (100 Gibbs sweeps): " + Arrays.toString(
        result.model().gibbsSample(new int[3], result.beta(), 100, new MersenneTwister(seed))));
    if (output != null) {
      new TrainedTernaryNetwork(result.model(), result.beta()).save(output);
      System.out.println("Saved inference checkpoint: " + output);
    }
    return 0;
  }
}
