package name.ncg777.statistics;

import static org.junit.Assert.*;
import org.apache.commons.math3.random.MersenneTwister;
import org.junit.Test;

public class BoltzmannDistributionTests {
  private static final double EPS = 1e-12;

  @Test
  public void matchesAnalyticWeightsAndThermodynamicIdentity() {
    double[] energies = {0, 1, 1};
    double beta = 2;
    var d = new BoltzmannDistribution(energies, beta);
    double z = 1 + 2 * Math.exp(-2);
    assertEquals(z, d.partitionFunction(), EPS);
    assertEquals(1 / z, d.probability(0), EPS);
    assertEquals(Math.log(z), d.logPartitionFunction(), EPS);
    assertEquals(d.expectation(energies) - d.entropy() / beta, d.freeEnergy(), EPS);
  }

  @Test
  public void handlesMultiplicityAndReferenceMasses() {
    var d = new BoltzmannDistribution(new double[] {0, 1}, 1, new double[] {1, 100});
    assertTrue(d.probability(1) > 0.97);
    var reference = new BoltzmannDistribution(new double[] {99, -50, 0}, 0,
        new double[] {0.2, 0.8, 0});
    assertArrayEquals(new double[] {0.2, 0.8, 0}, reference.probabilities(), EPS);
    assertEquals(0, reference.logPartitionFunction(), EPS);
    assertThrows(IllegalStateException.class, reference::freeEnergy);
  }

  @Test
  public void supportsEnergyOffsetsAndVeryColdDistributions() {
    var d = new BoltzmannDistribution(new double[] {-1000, 0, 1000}, 1000);
    assertArrayEquals(new double[] {1, 0, 0}, d.probabilities(), 0);
    assertEquals(1e6, d.logPartitionFunction(), EPS);
    assertEquals(0, d.entropy(), 0);
    assertEquals(-1000, d.freeEnergy(), EPS);
    assertEquals(-1e6, d.logProbability(1), EPS);
    var a = new BoltzmannDistribution(new double[] {0, 1, 2}, 0.5);
    var b = new BoltzmannDistribution(new double[] {1e6, 1e6 + 1, 1e6 + 2}, 0.5);
    assertArrayEquals(a.probabilities(), b.probabilities(), EPS);
    assertEquals(a.logPartitionFunction() - 0.5e6, b.logPartitionFunction(), EPS);
  }

  @Test
  public void retainsProbabilitiesWhenEnergyDifferencesOrLogPartitionOverflow() {
    var d = new BoltzmannDistribution(new double[] {-1e308, 1e308}, 1e-308);
    assertEquals(1 / (1 + Math.exp(-2)), d.probability(0), EPS);
    var equal = new BoltzmannDistribution(new double[] {-1e308, -1e308}, 10);
    assertArrayEquals(new double[] {0.5, 0.5}, equal.probabilities(), EPS);
    assertEquals(Double.POSITIVE_INFINITY, equal.logPartitionFunction(), 0);
    assertEquals(-1e308, equal.freeEnergy(), 0);
    var tinyMass = new BoltzmannDistribution(new double[] {0, 0}, 1,
        new double[] {Double.MIN_VALUE, Double.MIN_VALUE});
    assertArrayEquals(new double[] {0.5, 0.5}, tinyMass.probabilities(), EPS);
  }

  @Test
  public void forbiddenStatesStayExcludedAtZeroTemperatureParameter() {
    var d = new BoltzmannDistribution(new double[] {Double.POSITIVE_INFINITY, 7, 8}, 0,
        new double[] {1, 1, 0});
    assertArrayEquals(new double[] {0, 1, 0}, d.probabilities(), 0);
    var random = new MersenneTwister(42);
    for (int i = 0; i < 100; i++) assertEquals(1, d.sample(random));
  }

  @Test
  public void samplesReproduciblyAndDefendsItsArrays() {
    double[] energies = {0, 1};
    var d = new BoltzmannDistribution(energies, 1);
    energies[0] = 999;
    double[] probabilities = d.probabilities();
    probabilities[0] = 0;
    assertEquals(1 / (1 + Math.exp(-1)), d.probability(0), EPS);
    var first = new MersenneTwister(77);
    var second = new MersenneTwister(77);
    int count = 0;
    for (int i = 0; i < 20000; i++) {
      int sample = d.sample(first);
      assertEquals(sample, d.sample(second));
      if (sample == 0) count++;
    }
    assertEquals(d.probability(0), count / 20000.0, 0.015);
  }

  @Test
  public void rejectsInvalidOrEmptySupport() {
    assertThrows(IllegalArgumentException.class, () -> new BoltzmannDistribution(new double[0], 1));
    for (double beta : new double[] {-1, Double.NaN, Double.POSITIVE_INFINITY}) {
      assertThrows(IllegalArgumentException.class, () -> new BoltzmannDistribution(new double[] {0}, beta));
    }
    for (double energy : new double[] {Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY}) {
      assertThrows(IllegalArgumentException.class, () -> new BoltzmannDistribution(new double[] {energy}, 1));
    }
    for (double mass : new double[] {-1, 0, Double.NaN, Double.POSITIVE_INFINITY}) {
      assertThrows(IllegalArgumentException.class,
          () -> new BoltzmannDistribution(new double[] {0}, 1, new double[] {mass}));
    }
    assertThrows(IllegalArgumentException.class,
        () -> new BoltzmannDistribution(new double[] {0}, 1, new double[] {1, 1}));
    var d = new BoltzmannDistribution(new double[] {0}, 1);
    assertThrows(IllegalArgumentException.class, () -> d.expectation(new double[0]));
    assertThrows(IllegalArgumentException.class, () -> d.expectation(new double[] {Double.NaN}));
  }
}
