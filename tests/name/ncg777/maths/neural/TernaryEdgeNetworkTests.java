package name.ncg777.maths.neural;

import static org.junit.Assert.*;
import java.util.*;
import java.util.concurrent.CancellationException;
import org.junit.Test;
import name.ncg777.maths.neural.TernaryContours.Raster;

public class TernaryEdgeNetworkTests {
  @Test public void boundaryDefinitionHandlesTransparencyColourAndImageBorders() {
    int[] filled = new int[25]; Arrays.fill(filled, 1); Raster raster = new Raster(5, 5, filled);
    filled[12] = -1; assertEquals(1, raster.at(2, 2)); // Defensive copy.
    boolean[] edges = TernaryContours.boundaries(raster);
    assertTrue(edges[0]); assertFalse(edges[12]); assertEquals(0, raster.at(-1, 0));
    raster = new Raster(5, 5, filled); edges = TernaryContours.boundaries(raster);
    assertTrue(edges[12]); assertTrue(edges[7]); assertTrue(edges[11]);
    filled[12] = 0; edges = TernaryContours.boundaries(new Raster(5, 5, filled));
    assertFalse(edges[12]); assertTrue(edges[7]);
    assertArrayEquals(new boolean[25], TernaryContours.boundaries(new Raster(5, 5, new int[25])));
  }
  @Test public void learnsOnSmallImagesAndGeneralizesLocalRuleToLargerHeldOutImages() {
    List<Raster> training = new ArrayList<>(), testing = new ArrayList<>();
    for (int i = 0; i < 96; i++) training.add(TernaryContours.generate(8, 777 + i));
    for (int i = 0; i < 24; i++) testing.add(TernaryContours.generate(16, 10000 + i));
    int[] progress = {0}; var network = TernaryEdgeNetwork.train(training, 30, 777, e -> progress[0] = e);
    assertEquals(30, progress[0]); var score = network.evaluate(testing);
    System.out.println("Held-out ternary boundaries: " + score + "; F1=" + score.f1());
    assertTrue("Should learn the simple local rule", score.f1() > .95);
    double[] probabilities = network.predict(TernaryContours.generate(128, 3));
    assertEquals(128 * 128, probabilities.length);
    for (double p : probabilities) assertTrue(Double.isFinite(p) && p >= 0 && p <= 1);
    // Translation of an interior patch must produce exactly the same response.
    int[] a = new int[64], b = new int[256]; a[3 * 8 + 3] = 1; b[8 * 16 + 8] = 1;
    assertEquals(network.predict(new Raster(8, 8, a))[27], network.predict(new Raster(16, 16, b))[136], 0);
  }
  @Test public void deterministicTrainingAndResourceLimits() {
    var images = List.of(TernaryContours.generate(8, 1));
    var a = TernaryEdgeNetwork.train(images, 2, 3, null); var b = TernaryEdgeNetwork.train(images, 2, 3, null);
    assertArrayEquals(a.predict(images.get(0)), b.predict(images.get(0)), 0);
    assertThrows(IllegalArgumentException.class, () -> TernaryEdgeNetwork.train(images, 101, 0, null));
    assertThrows(IllegalArgumentException.class, () -> TernaryEdgeNetwork.train(List.of(), 1, 0, null));
    assertThrows(IllegalArgumentException.class, () -> TernaryEdgeNetwork.train(Collections.nCopies(2, TernaryContours.generate(128, 1)), 100, 0, null));
    assertThrows(IllegalArgumentException.class, () -> new Raster(129, 1, new int[129]));
    assertThrows(IllegalArgumentException.class, () -> new Raster(1, 1, new int[] {2}));
    Thread.currentThread().interrupt();
    try { assertThrows(CancellationException.class, () -> TernaryEdgeNetwork.train(images, 1, 0, null)); }
    finally { Thread.interrupted(); }
  }
}
