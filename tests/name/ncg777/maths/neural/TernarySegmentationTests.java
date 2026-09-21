package name.ncg777.maths.neural;

import static org.junit.Assert.*;
import java.util.*;
import java.util.concurrent.CancellationException;
import org.junit.Test;
import name.ncg777.maths.neural.TernarySegmentation.*;

public class TernarySegmentationTests {
  @Test public void cleanClosedContoursRecoverShapesHolesAndNestedIslands() {
    for (Shape shape : Shape.values()) if (shape != Shape.BORDER_CUT) {
      Example example = TernarySegmentation.generate(64, shape, 0, 0, 777);
      Regions result = TernarySegmentation.regions(64, 64, example.boundary());
      assertArrayEquals(shape.name(), example.regions(), result.trits());
      assertEquals(0, result.ambiguousContours()); assertFalse(result.touchesBorder());
      assertEquals(1, TernarySegmentation.score(example, result).iou(), 0);
    }
  }
  @Test public void borderCropsAreFlaggedAndNotPretendedToBeClosed() {
    Example example = TernarySegmentation.generate(32, Shape.BORDER_CUT, 0, 0, 1);
    Regions result = TernarySegmentation.regions(32, 32, example.boundary());
    assertTrue(result.touchesBorder()); assertTrue(result.ambiguousContours() > 0);
    assertEquals(-1, result.trits()[16 * 32 + 16]);
    assertEquals(0, TernarySegmentation.score(example, result).iou(), 0);
  }
  @Test public void aSmallGapLeaksUntilClosingAndDoesNotEraseAHole() {
    Example example = TernarySegmentation.generate(32, Shape.HOLE, 0, 0, 777);
    boolean[] boundary = example.boundary();
    int row = -1; for (int y = 0; y < 16; y++) if (boundary[y * 32 + 16]) { row = y; break; }
    assertTrue(row >= 0); boundary[row * 32 + 15] = boundary[row * 32 + 16] = false;
    Regions leaking = TernarySegmentation.regions(32, 32, boundary);
    assertTrue(leaking.ambiguousContours() > 0);
    boolean[] closed = TernarySegmentation.close(32, 32, boundary, 1);
    assertArrayEquals(example.boundary(), closed);
    assertArrayEquals(example.regions(), TernarySegmentation.regions(32, 32, closed).trits());
  }
  @Test public void labelsAreUnaffectedByCorruptionAndDataIsDefensivelyCopied() {
    var clean = TernarySegmentation.generate(32, Shape.HOLE, 0, 0, 7);
    var damaged = TernarySegmentation.generate(32, Shape.HOLE, 3, .02, 7);
    assertArrayEquals(clean.boundary(), damaged.boundary()); assertArrayEquals(clean.regions(), damaged.regions());
    assertFalse(Arrays.equals(clean.observed().pixels(), damaged.observed().pixels()));
    int[] first = damaged.regions(); first[0] = 42; assertEquals(-1, damaged.regions()[0]);
    assertArrayEquals(damaged.observed().pixels(), TernarySegmentation.generate(32, Shape.HOLE, 3, .02, 7).observed().pixels());
  }
  @Test public void emptyFullAndResourceLimits() {
    int[] outside = new int[16]; Arrays.fill(outside, -1);
    assertArrayEquals(outside, TernarySegmentation.regions(4, 4, new boolean[16]).trits());
    boolean[] full = new boolean[16]; Arrays.fill(full, true);
    assertArrayEquals(new int[16], TernarySegmentation.regions(4, 4, full).trits());
    assertArrayEquals(full, TernarySegmentation.close(4, 4, full, 2));
    assertThrows(IllegalArgumentException.class, () -> TernarySegmentation.generate(129, Shape.HOLE, 0, 0, 1));
    assertThrows(IllegalArgumentException.class, () -> TernarySegmentation.generate(32, Shape.HOLE, 0, Double.NaN, 1));
    assertThrows(IllegalArgumentException.class, () -> TernarySegmentation.regions(129, 1, new boolean[129]));
    assertThrows(IllegalArgumentException.class, () -> TernarySegmentation.close(4, 4, full, 3));
    Thread.currentThread().interrupt();
    try { assertThrows(CancellationException.class, () -> TernarySegmentation.regions(4, 4, full)); }
    finally { Thread.interrupted(); }
  }
}
