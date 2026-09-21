package name.ncg777.maths.physics;

import static org.junit.Assert.*;
import org.apache.commons.math3.random.MersenneTwister;
import org.junit.Test;
import name.ncg777.maths.Trit;
import name.ncg777.maths.lattices.BooleanLattice;

public class TernarySpinModelTests {
  private static final double EPS = 1e-11;

  private TernarySpinModel pair(double j, double delta, double h) {
    return new TernarySpinModel(new double[][] {{0, j}, {j, 0}},
        new double[] {delta, delta}, new double[] {h, 0});
  }

  @Test
  public void oneSiteMatchesClosedFormAndSeparatesNeutralityFromZeroMean() {
    double beta = 1.7, delta = 0.8, h = 0.3;
    var model = new TernarySpinModel(new double[][] {{0}}, new double[] {delta}, new double[] {h});
    var exact = model.exactDistribution(beta);
    double z = 1 + 2 * Math.exp(-beta * delta) * Math.cosh(beta * h);
    assertEquals(z, exact.distribution().partitionFunction(), EPS);
    assertEquals(2 * Math.exp(-beta * delta) * Math.sinh(beta * h) / z, exact.meanOrientation(0), EPS);
    assertEquals(1 - 1 / z, exact.participation(0), EPS);
    var unbiased = new TernarySpinModel(new double[][] {{0}}, new double[] {5}, new double[] {0})
        .exactDistribution(1);
    assertEquals(0, unbiased.meanOrientation(0), EPS);
    assertTrue(unbiased.participation(0) < 0.02);
  }

  @Test
  public void energyAndCorrelationRespectAttractionRepulsionAndNeutrality() {
    var attractive = pair(2, 0, 0);
    assertEquals(-2, attractive.energy(new int[] {1, 1}), 0);
    assertEquals(2, attractive.energy(new int[] {1, -1}), 0);
    assertEquals(0, attractive.energy(new int[] {0, 1}), 0);
    assertTrue(attractive.exactDistribution(1).correlation(0, 1) > 0);
    assertTrue(pair(-2, 0, 0).exactDistribution(1).correlation(0, 1) < 0);
    assertEquals(5 + 4 * Math.cosh(2), attractive.exactDistribution(1).distribution().partitionFunction(), EPS);
  }

  @Test
  public void independentSitesFactorizeAndInfiniteTemperatureIsUniform() {
    var exact = pair(0, 0.4, 0).exactDistribution(2);
    assertEquals(Math.pow(1 + 2 * Math.exp(-0.8), 2), exact.distribution().partitionFunction(), EPS);
    assertEquals(0, exact.covariance(0, 1), EPS);
    var uniform = pair(3, 5, 7).exactDistribution(0);
    assertEquals(Math.log(9), uniform.distribution().entropy(), EPS);
    assertEquals(2.0 / 3, uniform.participation(0), EPS);
  }

  @Test
  public void partitionDerivativesGenerateEnergyOrientationParticipationAndCorrelation() {
    double j = 0.7, delta = 0.4, h = 0.3, beta = 1.2, step = 1e-5;
    var exact = pair(j, delta, h).exactDistribution(beta);
    double dh = (pair(j, delta, h + step).exactDistribution(beta).distribution().logPartitionFunction()
        - pair(j, delta, h - step).exactDistribution(beta).distribution().logPartitionFunction()) / (2 * step);
    double dj = (pair(j + step, delta, h).exactDistribution(beta).distribution().logPartitionFunction()
        - pair(j - step, delta, h).exactDistribution(beta).distribution().logPartitionFunction()) / (2 * step);
    double dd = (pair(j, delta + step, h).exactDistribution(beta).distribution().logPartitionFunction()
        - pair(j, delta - step, h).exactDistribution(beta).distribution().logPartitionFunction()) / (2 * step);
    double db = (pair(j, delta, h).exactDistribution(beta + step).distribution().logPartitionFunction()
        - pair(j, delta, h).exactDistribution(beta - step).distribution().logPartitionFunction()) / (2 * step);
    assertEquals(beta * exact.meanOrientation(0), dh, 1e-9);
    assertEquals(beta * exact.correlation(0, 1), dj, 1e-9);
    assertEquals(-beta * (exact.participation(0) + exact.participation(1)), dd, 1e-9);
    assertEquals(-exact.meanEnergy(), db, 1e-9);
    double response = (pair(j, delta, h + step).exactDistribution(beta).meanOrientation(1)
        - pair(j, delta, h - step).exactDistribution(beta).meanOrientation(1)) / (2 * step);
    assertEquals(beta * exact.covariance(0, 1), response, 1e-9);
  }

  @Test
  public void tritConstraintsAndActiveSubsetMarginalsAgreeWithDirectEvents() {
    var model = pair(0.7, 0.4, 0.2);
    var exact = model.exactDistribution(1, s -> Trit.OR(s[0], s[1]) == 1, 9);
    assertEquals(5, exact.size());
    assertEquals(1, exact.probability(s -> Trit.OR(s[0], s[1]) == 1), EPS);
    double[] activeMass = new double[4];
    for (int i = 0; i < exact.size(); i++) {
      int[] s = exact.state(i);
      int mask = (s[0] == 0 ? 0 : 1) | (s[1] == 0 ? 0 : 2);
      activeMass[mask] += exact.distribution().probability(i);
    }
    double[] cumulative = BooleanLattice.zetaTransform(activeMass);
    assertEquals(exact.probability(s -> s[1] == 0), cumulative[1], EPS);
    assertEquals(1, cumulative[3], EPS);
    assertArrayEquals(activeMass, BooleanLattice.mobiusTransform(cumulative), EPS);
  }

  @Test
  public void localConditionalMatchesExactJointDistribution() {
    var model = pair(-0.8, 0.6, 0.2);
    var exact = model.exactDistribution(1.3);
    for (int neighbor = -1; neighbor <= 1; neighbor++) {
      final int fixed = neighbor;
      var conditional = model.conditionalDistribution(new int[] {0, neighbor}, 0, 1.3);
      double denominator = exact.probability(s -> s[1] == fixed);
      for (int value = -1; value <= 1; value++) {
        final int v = value;
        assertEquals(exact.probability(s -> s[0] == v && s[1] == fixed) / denominator,
            conditional.probability(value + 1), EPS);
      }
    }
  }

  @Test
  public void seededGibbsMatchesSmallModelStatistics() {
    var model = pair(0.8, 0.4, 0.2);
    var exact = model.exactDistribution(1);
    var random = new MersenneTwister(777);
    var repeat = new MersenneTwister(777);
    int[] state = model.gibbsSample(new int[2], 1, 100, random);
    assertArrayEquals(state, model.gibbsSample(new int[2], 1, 100, repeat));
    double mean = 0, participation = 0, correlation = 0;
    for (int i = 0; i < 30000; i++) {
      state = model.gibbsSample(state, 1, 1, random);
      mean += state[0];
      participation += state[0] * state[0];
      correlation += state[0] * state[1];
    }
    assertEquals(exact.meanOrientation(0), mean / 30000, 0.025);
    assertEquals(exact.participation(0), participation / 30000, 0.025);
    assertEquals(exact.correlation(0, 1), correlation / 30000, 0.025);
  }

  @Test
  public void protectsModelAndDistributionFromMutation() {
    double[][] j = {{0, 1}, {1, 0}};
    double[] delta = {0, 0}, h = {0, 0};
    var model = new TernarySpinModel(j, delta, h);
    j[0][1] = 99; delta[0] = 99; h[0] = 99;
    assertEquals(-1, model.energy(new int[] {1, 1}), 0);
    var exact = model.exactDistribution(1, s -> { s[0] = 99; return true; }, 9);
    int[] original = exact.state(0);
    exact.state(0)[0] = 99;
    exact.expectation(s -> { s[0] = 99; return 0; });
    exact.sample(new MersenneTwister(1))[0] = 99;
    assertArrayEquals(original, exact.state(0));
    int[] initial = {0, 0};
    model.gibbsSample(initial, 1, 10, new MersenneTwister(1));
    assertArrayEquals(new int[2], initial);
  }

  @Test
  public void emptyModelHasOneStateAndUnitPartition() {
    var model = new TernarySpinModel(new double[0][0], new double[0], new double[0]);
    var exact = model.exactDistribution(1);
    assertEquals(1, exact.size());
    assertEquals(1, exact.distribution().partitionFunction(), 0);
    assertEquals(0, exact.distribution().entropy(), 0);
    assertArrayEquals(new int[0], model.gibbsSample(new int[0], 1, 3, new MersenneTwister(1)));
  }

  @Test
  public void rejectsMalformedModelsStatesAndUnboundedEnumeration() {
    assertThrows(IllegalArgumentException.class, () -> new TernarySpinModel(
        new double[][] {{0, 1}, {2, 0}}, new double[2], new double[2]));
    assertThrows(IllegalArgumentException.class, () -> new TernarySpinModel(
        new double[][] {{1}}, new double[1], new double[1]));
    assertThrows(IllegalArgumentException.class, () -> new TernarySpinModel(
        new double[][] {{0}, {0}}, new double[2], new double[2]));
    assertThrows(IllegalArgumentException.class, () -> pair(Double.NaN, 0, 0));
    var model = pair(1, 0, 0);
    assertThrows(IllegalArgumentException.class, () -> model.energy(new int[] {2, 0}));
    assertThrows(IllegalArgumentException.class, () -> model.energy(new int[1]));
    assertThrows(IllegalArgumentException.class, () -> model.exactDistribution(1, s -> true, 8));
    assertThrows(IllegalArgumentException.class, () -> model.exactDistribution(1, s -> false, 9));
    assertThrows(IllegalArgumentException.class, () -> model.exactDistribution(Double.NaN));
    assertThrows(IllegalArgumentException.class,
        () -> model.gibbsSample(new int[2], 1, -1, new MersenneTwister(1)));
    assertThrows(IllegalArgumentException.class, () -> new TernarySpinModel(
        new double[30][30], new double[30], new double[30]).exactDistribution(1));
  }
}
