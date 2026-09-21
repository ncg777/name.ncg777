package name.ncg777.maths.neural;

import static org.junit.Assert.*;
import java.util.*;
import java.util.concurrent.CancellationException;
import org.junit.BeforeClass;
import org.junit.Test;
import name.ncg777.maths.neural.TernaryContours.Raster;
import name.ncg777.maths.neural.TernarySegmentation.*;

public class TernaryPatchNetworkTests {
  private static TernaryPatchNetwork network;
  @BeforeClass public static void fit() { network = TernarySegmentationExperiment.train(null); }

  @Test public void overlappingPatchesCoverEveryPixelIncludingOddDimensionsAndBorders() {
    for (int side : new int[] {1, 8, 25, 128}) {
      var result = network.predict(new Raster(side, side + (side < 128 ? 1 : 0), new int[side * (side + (side < 128 ? 1 : 0))]));
      for (int n : result.votes()) assertEquals(4, n);
      for (double value : result.scores()) assertTrue(Double.isFinite(value) && value >= 0 && value <= 1);
      for (double value : result.disagreement()) assertTrue(Double.isFinite(value) && value >= 0 && value <= .5 + 1e-12);
      double original = result.scores()[0]; result.scores()[0] = 42; assertEquals(original, result.scores()[0], 0);
      assertThrows(IllegalArgumentException.class, () -> result.threshold(Double.NaN));
    }
  }
  @Test public void sharedFiltersTranslateWithTheStrideAndIgnoreStrokeColour() {
    int[] a = new int[32 * 32], b = new int[a.length], negative = new int[a.length];
    for (int y = 10; y < 20; y++) { a[y * 32 + 10] = 1; b[(y + 2) * 32 + 12] = 1; negative[y * 32 + 10] = -1; }
    var p = network.predict(new Raster(32, 32, a)); var q = network.predict(new Raster(32, 32, b));
    assertEquals(p.scores()[15 * 32 + 10], q.scores()[17 * 32 + 12], 1e-12);
    assertArrayEquals(p.scores(), network.predict(new Raster(32, 32, negative)).scores(), 0);
  }
  @Test public void heldOutComparisonReportsBoundaryAndRegionMetricsWithoutTrainingLeakage() {
    var rows = TernarySegmentationExperiment.evaluate(network, .5, 1); assertEquals(9, rows.size());
    for (var row : rows) {
      assertEquals(6, row.images()); assertTrue(row.score().f1() >= 0 && row.score().f1() <= 1);
      assertTrue(row.score().iou() >= 0 && row.score().iou() <= 1);
      System.out.printf(Locale.ROOT, "%s / %s: F1 %.4f, IoU %.4f, ambiguous %d/%d%n", row.condition(), row.method(), row.score().f1(), row.score().iou(), row.ambiguousImages(), row.images());
    }
    assertEquals(1, rows.get(0).score().f1(), 0);
    assertTrue("Border crop must remain in evaluation", rows.get(0).score().iou() < 1);
    assertTrue("Learned contour should retain useful signal", rows.get(2).score().f1() > .7);
    // Restore accepts only observed pixels; changing truth cannot affect its predictions.
    var example = TernarySegmentation.generate(32, Shape.HOLE, 2, .02, 7);
    var predicted = TernarySegmentationExperiment.restore(network, example.observed(), .5, 0);
    assertArrayEquals(network.predict(example.observed()).threshold(.5), boundary(predicted.learned().trits()));
  }
  private static boolean[] boundary(int[] trits) { boolean[] b = new boolean[trits.length]; for (int i = 0; i < b.length; i++) b[i] = trits[i] == 0; return b; }
  @Test public void deterministicTrainingAndLimits() {
    var examples = List.of(TernarySegmentation.generate(24, Shape.RECTANGLE, 1, .01, 1));
    var a = TernaryPatchNetwork.train(examples, 1, 1, null); var b = TernaryPatchNetwork.train(examples, 1, 1, null);
    assertArrayEquals(a.predict(examples.get(0).observed()).scores(), b.predict(examples.get(0).observed()).scores(), 0);
    assertThrows(IllegalArgumentException.class, () -> TernaryPatchNetwork.train(Collections.nCopies(128, examples.get(0)), 50, 1, null));
    Thread.currentThread().interrupt();
    try {
      assertThrows(CancellationException.class, () -> TernaryPatchNetwork.train(examples, 1, 1, null));
      assertThrows(CancellationException.class, () -> network.predict(examples.get(0).observed()));
    } finally { Thread.interrupted(); }
  }
}
