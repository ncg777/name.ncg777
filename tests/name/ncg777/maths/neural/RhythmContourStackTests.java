package name.ncg777.maths.neural;

import static org.junit.Assert.*;
import java.util.*;
import java.util.concurrent.CancellationException;
import org.junit.BeforeClass;
import org.junit.Test;
import name.ncg777.maths.numbers.*;
import name.ncg777.maths.numbers.predicates.ShadowContourIsomorphic;
import name.ncg777.maths.numbers.relations.PredicatedDifferences;

public class RhythmContourStackTests {
  private static RhythmContourStacks.Dataset data;
  @BeforeClass public static void load() { data = RhythmContourStacks.dataset(null); }
  @Test public void fourBarsRespectTheMatrixGeneratorsAllPreviousRowRule() {
    assertEquals(192, data.training().size()); assertEquals(64, data.testing().size());
    Set<String> trainingHex = new HashSet<>(), testingHex = new HashSet<>(), samples = new HashSet<>();
    var all = new ArrayList<>(data.training()); all.addAll(data.testing()); var sci = new ShadowContourIsomorphic(); var differences = new PredicatedDifferences(sci);
    for (var stack : all) {
      assertEquals(4, stack.hex().size()); assertEquals(64, stack.values().length); assertTrue(samples.add(Arrays.toString(stack.values())));
      for (int row = 0; row < 4; row++) {
        var rhythm = new Natural(Cipher.Name.Hexadecimal, stack.hex().get(row)).toBinaryNatural(); assertTrue(sci.apply(rhythm));
        for (int prior = 0; prior < row; prior++) assertTrue(differences.test(rhythm, new Natural(Cipher.Name.Hexadecimal, stack.hex().get(prior)).toBinaryNatural()));
        assertArrayEquals(RhythmContourStacks.samples(RhythmContourEmbeddings.example(stack.hex().get(row))), Arrays.copyOfRange(stack.values(), row * 16, (row + 1) * 16), 0);
      }
    }
    data.training().forEach(s -> trainingHex.addAll(s.hex())); data.testing().forEach(s -> testingHex.addAll(s.hex()));
    assertTrue(Collections.disjoint(trainingHex, testingHex));
    System.out.println("Four-bar contour stacks: 192/64; rhythm pools " + trainingHex.size() + "/" + testingHex.size());
  }
  @Test public void curveDecoderUsesOnlyTritsAndReturnsContinuousHeights() {
    double[][] training = data.training().stream().map(RhythmContourStacks.Stack::values).toArray(double[][]::new);
    for (int width : new int[] {8,16,32,64}) {
      var model = TernaryCurveAutoencoder.train(training, width, 80, 777); double error = 0;
      for (var stack : data.testing()) {
        int[] code = model.encode(stack.values()); assertEquals(width, code.length); for (int t : code) assertTrue(t >= -1 && t <= 1);
        double[] decoded = model.decode(code); for (double d : decoded) assertTrue(Double.isFinite(d) && d >= -1 && d <= 1);
        assertArrayEquals(decoded, model.decode(code), 0); error += TernaryCurveAutoencoder.error(stack.values(), decoded);
      }
      System.out.printf(Locale.ROOT, "Curve model %d trits: clean RMSE %.2f%%%n", width, Math.sqrt(error / 64) * 100);
      assertTrue(error / 64 < .25);
    }
  }
  @Test public void noiseValidationAndTrainingLimits() {
    double[] input = data.testing().get(0).values(); assertArrayEquals(input, TernaryCurveAutoencoder.noise(input, 0, 777), 0);
    assertThrows(IllegalArgumentException.class, () -> TernaryCurveAutoencoder.noise(input, Double.NaN, 1));
    assertThrows(IllegalArgumentException.class, () -> TernaryCurveAutoencoder.train(new double[1024][64], 64, 200, 777));
    Thread.currentThread().interrupt();
    try { assertThrows(CancellationException.class, () -> TernaryCurveAutoencoder.train(new double[][] {input}, 8, 1, 777)); }
    finally { Thread.interrupted(); }
  }
}
