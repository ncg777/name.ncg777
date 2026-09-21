package name.ncg777.maths.physics.apps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import org.apache.commons.math3.random.MersenneTwister;
import name.ncg777.maths.Trit;
import name.ncg777.maths.enumerations.WordEnumeration;
import name.ncg777.maths.lattices.BooleanLattice;
import name.ncg777.maths.physics.TernarySpinModel;
import name.ncg777.statistics.BoltzmannDistribution;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** Small, reproducible examples of equilibrium statistics, generation and planning. */
@Command(name = "partition-functions", mixinStandardHelpOptions = true,
    description = "Explore ternary spin statistics, constrained generation, lattice marginals and trajectory costs.")
public class PartitionFunctionDemo implements Runnable {
  @Option(names = "--beta", defaultValue = "1", description = "Finite inverse temperature >= 0 (default: 1).")
  private double beta;
  @Option(names = "--seed", defaultValue = "777", description = "Random seed (default: 777).")
  private long seed;

  public static void main(String[] args) {
    System.exit(new CommandLine(new PartitionFunctionDemo()).execute(args));
  }

  @Override
  public void run() {
    if (!Double.isFinite(beta) || beta < 0) throw new IllegalArgumentException("beta must be finite and >= 0");
    var random = new MersenneTwister(seed);
    var model = new TernarySpinModel(new double[][] {
        {0, 1.2, -0.8}, {1.2, 0, 0.4}, {-0.8, 0.4, 0}},
        new double[] {0.3, 0.6, 0.3}, new double[] {0.2, 0, -0.1});
    var exact = model.exactDistribution(beta);
    System.out.printf(Locale.ROOT, "log Z = %.6f; entropy = %.6f; mean energy = %.6f%n",
        exact.distribution().logPartitionFunction(), exact.distribution().entropy(), exact.meanEnergy());
    for (int i = 0; i < model.size(); i++) {
      System.out.printf(Locale.ROOT, "Node %d: orientation = %.6f; participation = %.6f%n",
          i, exact.meanOrientation(i), exact.participation(i));
    }
    var local = model.conditionalDistribution(new int[] {0, 1, -1}, 0, beta);
    System.out.printf(Locale.ROOT, "Thermal activation at node 0 given [*, 1, -1]: %.6f%n",
        local.expectation(new double[] {-1, 0, 1}));
    System.out.println("Gibbs sample after 100 sweeps (approximate): "
        + Arrays.toString(model.gibbsSample(new int[3], beta, 100, random)));

    // The existing Trit.OR chooses the logic. Probability is over valuations.
    var constrained = model.exactDistribution(beta, s -> Trit.OR(s[0], s[1]) == 1, 27);
    System.out.println("Generated pattern satisfying OR(s0,s1)=+1: "
        + Arrays.toString(constrained.sample(random)));

    double[] activeMass = new double[1 << model.size()];
    for (int i = 0; i < exact.size(); i++) {
      int[] s = exact.state(i);
      int mask = 0;
      for (int j = 0; j < s.length; j++) if (s[j] != 0) mask |= 1 << j;
      activeMass[mask] += exact.distribution().probability(i);
    }
    // Zeta at mask S gives P(active set is contained in S).
    double[] cumulative = BooleanLattice.zetaTransform(activeMass);
    System.out.printf(Locale.ROOT, "P(active set contained in {0,1}) = %.6f%n", cumulative[3]);
    System.out.println("Recovered active-set probabilities: "
        + Arrays.toString(BooleanLattice.mobiusTransform(cumulative)));

    // Enumerate four-step controls u in {-1,0,+1}, x(t+1)=x(t)+u(t), x(0)=0.
    // Every intermediate position must remain in [0,3]; the target is x=2.
    // The prior is uniform over all 81 paths. Excluded paths have infinite cost.
    var words = new WordEnumeration(4, 3);
    var paths = new ArrayList<int[]>();
    double[] costs = new double[81], prior = new double[81];
    Arrays.fill(prior, 1.0 / 81);
    while (words.hasMoreElements()) {
      int[] actions = words.nextElement();
      int position = 0;
      double cost = 0;
      boolean feasible = true;
      for (int t = 0; t < actions.length; t++) {
        actions[t]--;
        position += actions[t];
        feasible &= position >= 0 && position <= 3;
        cost += 0.1 * actions[t] * actions[t];
      }
      cost += (position - 2) * (position - 2);
      costs[paths.size()] = feasible ? cost : Double.POSITIVE_INFINITY;
      paths.add(actions);
    }
    var trajectories = new BoltzmannDistribution(costs, beta, prior);
    int[] chosen = paths.get(trajectories.sample(random));
    System.out.printf(Locale.ROOT, "Trajectory log Z = %.6f; feasible sampled controls = %s; first action = %d%n",
        trajectories.logPartitionFunction(), Arrays.toString(chosen), chosen[0]);
    // Replan from the observed next position in a receding-horizon application.

    // Learn a single coupling from target agreement using the exact likelihood gradient.
    // Fixed beta=1 keeps this learning example meaningful even when --beta=0.
    double coupling = 0;
    for (int iteration = 0; iteration < 100; iteration++) {
      var fit = pair(coupling).exactDistribution(1);
      coupling += 0.2 * (0.5 - fit.correlation(0, 1));
    }
    System.out.printf(Locale.ROOT, "Learned coupling = %.6f; agreement = %.6f (target 0.5, beta=1)%n",
        coupling, pair(coupling).exactDistribution(1).correlation(0, 1));
  }

  private static TernarySpinModel pair(double coupling) {
    return new TernarySpinModel(new double[][] {{0, coupling}, {coupling, 0}},
        new double[2], new double[2]);
  }
}
