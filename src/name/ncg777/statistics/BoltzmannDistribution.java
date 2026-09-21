package name.ncg777.statistics;

import java.util.Arrays;
import java.util.Objects;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * A finite distribution p(i) = mass(i) exp(-beta energy(i)) / Z.
 * Masses need not sum to one: use unit masses for a partition sum, or a
 * normalized reference distribution for exponential tilting of trajectory costs.
 * Positive infinite energies and zero masses exclude states, even at beta = 0.
 * All logarithms and entropy use natural units (nats).
 */
public final class BoltzmannDistribution {
  private final double beta;
  private final double minimumEnergy;
  private final double logRelativePartition;
  private final double[] probabilities;
  private final double[] logProbabilities;

  public BoltzmannDistribution(double[] energies, double beta) {
    this(energies, beta, unitMasses(energies.length));
  }

  public BoltzmannDistribution(double[] energies, double beta, double[] masses) {
    Objects.requireNonNull(energies);
    Objects.requireNonNull(masses);
    if (!Double.isFinite(beta) || beta < 0) {
      throw new IllegalArgumentException("beta must be finite and nonnegative");
    }
    if (energies.length == 0 || energies.length != masses.length) {
      throw new IllegalArgumentException("Nonempty energy and mass arrays must have equal lengths");
    }
    this.beta = beta;
    double minimum = Double.POSITIVE_INFINITY;
    for (int i = 0; i < energies.length; i++) {
      if (Double.isNaN(energies[i]) || energies[i] == Double.NEGATIVE_INFINITY
          || !Double.isFinite(masses[i]) || masses[i] < 0) {
        throw new IllegalArgumentException("Invalid energy or reference mass");
      }
      if (masses[i] > 0) minimum = Math.min(minimum, energies[i]);
    }
    if (!Double.isFinite(minimum)) {
      throw new IllegalArgumentException("At least one finite-energy state must have positive mass");
    }
    minimumEnergy = minimum;
    logProbabilities = new double[energies.length];
    double maximum = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < energies.length; i++) {
      double logWeight = Double.NEGATIVE_INFINITY;
      if (masses[i] > 0 && Double.isFinite(energies[i])) {
        double gap = energies[i] - minimum;
        // Subtract before multiplying to retain small differences at large offsets.
        // If subtraction overflows, scale first (important for very small beta).
        double cost = beta == 0 ? 0 : Double.isFinite(gap)
            ? beta * gap : beta * energies[i] - beta * minimum;
        logWeight = Math.log(masses[i]) - cost;
      }
      logProbabilities[i] = logWeight;
      maximum = Math.max(maximum, logWeight);
    }
    double total = 0;
    for (double weight : logProbabilities) total += Math.exp(weight - maximum);
    double logTotal = Math.log(total);
    logRelativePartition = maximum + logTotal;
    probabilities = new double[energies.length];
    for (int i = 0; i < energies.length; i++) {
      logProbabilities[i] = (logProbabilities[i] - maximum) - logTotal;
      probabilities[i] = Math.exp(logProbabilities[i]);
    }
  }

  private static double[] unitMasses(int size) {
    double[] masses = new double[size];
    Arrays.fill(masses, 1);
    return masses;
  }

  public int size() { return probabilities.length; }
  public double probability(int index) { return probabilities[index]; }
  public double logProbability(int index) { return logProbabilities[index]; }
  public double[] probabilities() { return probabilities.clone(); }

  /** May overflow to an infinity when the mathematical log Z exceeds double range. */
  public double logPartitionFunction() { return -beta * minimumEnergy + logRelativePartition; }

  /** Z itself can overflow even when log Z and all probabilities are well behaved. */
  public double partitionFunction() { return Math.exp(logPartitionFunction()); }

  /** Undefined at beta = 0; computed without multiplying beta by the energy offset. */
  public double freeEnergy() {
    if (beta == 0) throw new IllegalStateException("Free energy requires positive beta");
    return minimumEnergy - logRelativePartition / beta;
  }

  public double entropy() {
    double result = 0;
    for (int i = 0; i < size(); i++) {
      if (probabilities[i] > 0) result -= probabilities[i] * logProbabilities[i];
    }
    return result;
  }

  public double expectation(double[] values) {
    if (values.length != size()) throw new IllegalArgumentException("Observable length mismatch");
    double result = 0;
    for (int i = 0; i < size(); i++) {
      if (!Double.isFinite(values[i])) throw new IllegalArgumentException("Observable must be finite");
      result += probabilities[i] * values[i];
    }
    return result;
  }

  /** Draws an index using an explicit, caller-owned random generator. */
  public int sample(RandomGenerator random) {
    Objects.requireNonNull(random);
    double draw = random.nextDouble();
    double cumulative = 0;
    int lastSupported = 0;
    for (int i = 0; i < size(); i++) {
      if (probabilities[i] == 0) continue;
      lastSupported = i;
      cumulative += probabilities[i];
      if (draw < cumulative) return i;
    }
    return lastSupported; // Account for roundoff in the cumulative sum.
  }
}
