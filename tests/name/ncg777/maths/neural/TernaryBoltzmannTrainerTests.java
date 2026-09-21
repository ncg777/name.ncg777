package name.ncg777.maths.neural;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.concurrent.CancellationException;
import org.junit.Test;
import name.ncg777.maths.neural.TernaryTrainingOptions.Mode;
import name.ncg777.maths.physics.TernarySpinModel;

public class TernaryBoltzmannTrainerTests {
  private TernarySpinModel zero(int n) {
    return new TernarySpinModel(new double[n][n], new double[n], new double[n]);
  }

  private double likelihood(TernarySpinModel model, int[][] data, double beta) {
    double logZ = model.exactDistribution(beta).distribution().logPartitionFunction();
    return Arrays.stream(data).mapToDouble(s -> -beta * model.energy(s) - logZ).average().getAsDouble();
  }

  private TernarySpinModel pair(double[] p) {
    return new TernarySpinModel(new double[][] {{0, p[0]}, {p[0], 0}},
        new double[] {p[1], p[2]}, new double[] {p[3], p[4]});
  }

  @Test
  public void allParameterUpdatesMatchFiniteDifferenceLikelihoodGradients() {
    double[] p = {0.3, 0.4, -0.2, 0.1, -0.3};
    int[][] data = {{1, 1}, {1, 0}, {0, -1}, {0, 0}, {-1, 1}};
    double beta = 1.3, rate = 0.01, epsilon = 1e-5;
    var result = TernaryBoltzmannTrainer.train(pair(p), data,
        TernaryTrainingOptions.builder().epochs(1).learningRate(rate).beta(beta).l2(0).build());
    var fit = result.model();
    double[] actual = {fit.couplings()[0][1], fit.penalties()[0], fit.penalties()[1], fit.fields()[0], fit.fields()[1]};
    for (int k = 0; k < p.length; k++) {
      double[] plus = p.clone(), minus = p.clone();
      plus[k] += epsilon; minus[k] -= epsilon;
      double gradient = (likelihood(pair(plus), data, beta) - likelihood(pair(minus), data, beta)) / (2 * epsilon);
      assertEquals(p[k] + rate * gradient, actual[k], 1e-10);
    }
    assertEquals(0, fit.couplings()[0][0], 0);
    assertEquals(fit.couplings()[0][1], fit.couplings()[1][0], 0);
    assertEquals(likelihood(fit, data, beta), result.history().get(1).trainingScore(), 1e-12);
  }

  @Test
  public void learnsAnalyticOneNodeDistributionIncludingNeutrality() {
    int[][] data = {{-1}, {-1}, {0}, {0}, {0}, {0}, {0}, {1}, {1}, {1}};
    var result = TernaryBoltzmannTrainer.train(zero(1), data,
        TernaryTrainingOptions.builder().epochs(400).learningRate(0.2).l2(0).build());
    assertEquals(0.5 * Math.log(1.5), result.model().fields()[0], 1e-5);
    assertEquals(Math.log(0.5 / Math.sqrt(0.2 * 0.3)), result.model().penalties()[0], 1e-5);
    assertArrayEquals(new double[] {0.2, 0.5, 0.3},
        result.model().conditionalDistribution(new int[1], 0, result.beta()).probabilities(), 1e-5);
    assertTrue(result.history().get(400).trainingScore() > result.history().get(0).trainingScore());
    assertEquals(400, result.selectedEpoch());
  }

  @Test
  public void learnsAgreementOppositionAndGeneratesMatchingPatterns() {
    int[][] data = {{1, 1, -1}, {1, 1, -1}, {-1, -1, 1}, {-1, -1, 1}, {0, 0, 0}};
    var result = TernaryBoltzmannTrainer.train(zero(3), data,
        TernaryTrainingOptions.builder().epochs(800).learningRate(0.1).build());
    assertTrue(result.model().couplings()[0][1] > 0);
    assertTrue(result.model().couplings()[0][2] < 0);
    var exact = result.model().exactDistribution(1);
    double patternProbability = exact.probability(s -> s[0] == s[1] && s[0] == -s[2]);
    assertTrue("Pattern probability: " + patternProbability, patternProbability > 0.85);
    assertTrue(result.history().get(800).trainingScore() > result.history().get(0).trainingScore() + 1);
  }

  @Test
  public void sampledTrainingIsReproducibleAndApproximatesKnownFrequencies() {
    int[][] data = {{-1}, {-1}, {0}, {0}, {0}, {0}, {0}, {1}, {1}, {1}};
    var options = TernaryTrainingOptions.builder().mode(Mode.PERSISTENT_GIBBS).epochs(250)
        .particles(256).sweeps(2).learningRate(0.2).l2(0).seed(123).build();
    var a = TernaryBoltzmannTrainer.train(zero(1), data, options);
    var b = TernaryBoltzmannTrainer.train(zero(1), data, options);
    assertEquals(a.history(), b.history());
    assertArrayEquals(a.model().fields(), b.model().fields(), 0);
    assertArrayEquals(new double[] {0.2, 0.5, 0.3},
        a.model().conditionalDistribution(new int[1], 0, a.beta()).probabilities(), 0.025);
    assertTrue(a.history().get(250).trainingScore() > a.history().get(0).trainingScore());
  }

  @Test
  public void sampledModeWorksBeyondExactEnumerationLimitAndLabelsScores() {
    int[][] data = {new int[13], new int[13]};
    Arrays.fill(data[1], 1);
    var options = TernaryTrainingOptions.builder().mode(Mode.PERSISTENT_GIBBS).epochs(3)
        .particles(16).burnIn(2).maxExactStates(1).build();
    var result = TernaryBoltzmannTrainer.train(zero(13), data, options);
    assertEquals(Mode.PERSISTENT_GIBBS, result.mode());
    assertEquals(-Math.log(3), result.history().get(0).trainingScore(), 1e-12);
    assertEquals(4, result.history().size());
    double score = 0;
    for (int[] row : data) for (int site = 0; site < 13; site++) {
      score += result.model().conditionalDistribution(row, site, 1).logProbability(row[site] + 1) / 26;
    }
    assertEquals(score, result.history().get(3).trainingScore(), 1e-12);
  }

  @Test
  public void validationRestoresInitialCheckpointWhenTrainingMovesAwayFromHeldOutData() {
    var initial = zero(1);
    var result = TernaryBoltzmannTrainer.train(initial, new int[][] {{-1}}, new int[][] {{1}},
        TernaryTrainingOptions.builder().epochs(100).patience(3).build());
    assertTrue(result.earlyStopped());
    assertSame(initial, result.model());
    assertEquals(0, result.selectedEpoch());
    assertEquals(4, result.history().size());
    assertEquals(-Math.log(3), result.history().get(0).validationScore().getAsDouble(), 1e-12);
  }

  @Test
  public void heldOutRowsDoNotChangeGradientsOrSampleInitialization() {
    for (Mode mode : Mode.values()) {
      var options = TernaryTrainingOptions.builder().mode(mode).epochs(1).l2(0).build();
      var a = TernaryBoltzmannTrainer.train(zero(1), new int[][] {{1}}, options);
      var b = TernaryBoltzmannTrainer.train(zero(1), new int[][] {{1}}, new int[][] {{1}, {1}}, options);
      assertEquals(a.history().get(1).trainingScore(), b.history().get(1).trainingScore(), 0);
      assertArrayEquals(a.model().fields(), b.model().fields(), 0);
    }
  }

  @Test
  public void decayUsesEachUndirectedParameterOnceAndClippingPreservesSymmetry() {
    double[] p = {0.3, 0.4, -0.2, 0.1, -0.3};
    int[][] data = {{1, 1}, {-1, 0}};
    var plain = TernaryBoltzmannTrainer.train(pair(p), data,
        TernaryTrainingOptions.builder().epochs(1).learningRate(0.1).l2(0).build()).model();
    var decay = TernaryBoltzmannTrainer.train(pair(p), data,
        TernaryTrainingOptions.builder().epochs(1).learningRate(0.1).l2(0.5).build()).model();
    assertEquals(plain.couplings()[0][1] - 0.05 * p[0], decay.couplings()[0][1], 1e-12);
    for (int i = 0; i < 2; i++) {
      assertEquals(plain.penalties()[i] - 0.05 * p[i + 1], decay.penalties()[i], 1e-12);
      assertEquals(plain.fields()[i] - 0.05 * p[i + 3], decay.fields()[i], 1e-12);
    }
    var clipped = TernaryBoltzmannTrainer.train(zero(2), new int[][] {{1, 1}},
        TernaryTrainingOptions.builder().epochs(1).learningRate(100).maxAbsParameter(0.1).build()).model();
    assertEquals(0.1, clipped.fields()[0], 0);
    assertEquals(-0.1, clipped.penalties()[0], 0);
    assertEquals(0.1, clipped.couplings()[0][1], 0);
    assertEquals(clipped.couplings()[0][1], clipped.couplings()[1][0], 0);
  }

  @Test
  public void respectsStateBudgetWorkBudgetAndNodeAndDataLimits() {
    var defaults = TernaryTrainingOptions.builder().build();
    assertThrows(IllegalArgumentException.class,
        () -> TernaryBoltzmannTrainer.train(zero(9), new int[1][9], defaults));
    assertThrows(IllegalArgumentException.class,
        () -> TernaryBoltzmannTrainer.train(zero(65), new int[1][65], defaults));
    assertThrows(IllegalArgumentException.class,
        () -> TernaryBoltzmannTrainer.train(zero(1), new int[100001][1], defaults));
    assertThrows(IllegalArgumentException.class, () -> TernaryBoltzmannTrainer.train(zero(2), new int[1][2],
        TernaryTrainingOptions.builder().maxWork(1).build()));
    var result = TernaryBoltzmannTrainer.train(zero(1), new int[1][1],
        TernaryTrainingOptions.builder().epochs(1).build());
    assertTrue(result.estimatedWork() > 0);
    assertThrows(IllegalArgumentException.class, () -> TernaryBoltzmannTrainer.train(zero(1), new int[1][1],
        TernaryTrainingOptions.builder().epochs(1).maxWork(result.estimatedWork() - 1).build()));
  }

  @Test
  public void rejectsInvalidDataAndSettingsBeforeTraining() {
    var options = TernaryTrainingOptions.builder().build();
    for (int[][] rows : new int[][][] {{}, {{2}}, {{1, 0}}, {null}}) {
      assertThrows(IllegalArgumentException.class, () -> TernaryBoltzmannTrainer.train(zero(1), rows, options));
      assertThrows(IllegalArgumentException.class,
          () -> TernaryBoltzmannTrainer.train(zero(1), new int[][] {{0}}, rows, options));
    }
    assertThrows(IllegalArgumentException.class, () -> TernaryTrainingOptions.builder().beta(0).build());
    assertThrows(IllegalArgumentException.class, () -> TernaryTrainingOptions.builder().learningRate(Double.NaN).build());
    assertThrows(IllegalArgumentException.class, () -> TernaryTrainingOptions.builder().l2(-1).build());
    assertThrows(IllegalArgumentException.class, () -> TernaryTrainingOptions.builder().epochs(0).build());
    assertThrows(IllegalArgumentException.class, () -> TernaryTrainingOptions.builder().maxExactStates(19684).build());
    assertThrows(IllegalArgumentException.class, () -> TernaryTrainingOptions.builder().sweeps(0).build());
    assertThrows(IllegalArgumentException.class, () -> TernaryBoltzmannTrainer.train(
        pair(new double[] {10, 0, 0, 0, 0}), new int[1][2], options));
  }

  @Test
  public void doesNotMutateInputsAndExposesOnlyDefensiveParametersAndHistory() {
    var initial = zero(2);
    int[][] data = {{1, -1}};
    var result = TernaryBoltzmannTrainer.train(initial, data,
        TernaryTrainingOptions.builder().epochs(2).build());
    assertArrayEquals(new int[] {1, -1}, data[0]);
    assertArrayEquals(new double[2], initial.fields(), 0);
    double field = result.model().fields()[0];
    result.model().fields()[0] = 99;
    result.model().penalties()[0] = 99;
    result.model().couplings()[0][1] = 99;
    assertEquals(field, result.model().fields()[0], 0);
    assertTrue(Math.abs(result.model().penalties()[0]) < 1);
    assertTrue(Math.abs(result.model().couplings()[0][1]) < 1);
    assertThrows(UnsupportedOperationException.class, () -> result.history().clear());
  }

  @Test
  public void respondsToInterruptionWithoutClearingInterruptFlag() {
    Thread.currentThread().interrupt();
    try {
      assertThrows(CancellationException.class, () -> TernaryBoltzmannTrainer.train(zero(1),
          new int[1][1], TernaryTrainingOptions.builder().build()));
      assertTrue(Thread.currentThread().isInterrupted());
    } finally {
      Thread.interrupted();
    }
  }

  @Test
  public void rejectsNonfiniteOptimizerArithmetic() {
    assertThrows(ArithmeticException.class, () -> TernaryBoltzmannTrainer.train(zero(1), new int[][] {{1}},
        TernaryTrainingOptions.builder().epochs(2).learningRate(Double.MAX_VALUE).beta(2).build()));
  }
}
