package name.ncg777.maths.neural;

import static org.junit.Assert.*;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.util.Arrays;
import org.junit.Test;

public class TernaryImageTests {
  @Test public void pngRoundTripPreservesOrientationAndAlpha() throws Exception {
    BufferedImage image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
    image.setRGB(2, 5, 0xff000000); image.setRGB(6, 1, 0xffffffff); image.setRGB(0, 0, 0x00123456);
    var cue = TernaryImage.decode(image);
    assertEquals(-1, cue.values()[42]); assertEquals(1, cue.values()[14]); assertEquals(0, cue.values()[0]);
    for (boolean known : cue.known()) assertTrue(known);
    var file = Files.createTempFile("ternary-image", ".png");
    try {
      TernaryImage.write(file, cue); var restored = TernaryImage.read(file);
      assertArrayEquals(cue.values(), restored.values()); assertArrayEquals(cue.known(), restored.known());
      assertEquals(0, TernaryImage.encode(restored).getRGB(0, 0));
    } finally { Files.deleteIfExists(file); }
  }
  @Test public void rejectsLossyColoursDimensionsAndMissingPixels() {
    assertThrows(IllegalArgumentException.class, () -> TernaryImage.decode(new BufferedImage(9, 8, 2)));
    BufferedImage image = new BufferedImage(8, 8, 2); image.setRGB(0, 0, 0xff777777);
    assertThrows(IllegalArgumentException.class, () -> TernaryImage.decode(image));
    image.setRGB(0, 0, 0x80ffffff); assertThrows(IllegalArgumentException.class, () -> TernaryImage.decode(image));
    boolean[] known = new boolean[64]; Arrays.fill(known, true); known[10] = false;
    assertThrows(IllegalArgumentException.class, () -> TernaryImage.encode(new TernaryAssociativeMemory.Cue(new int[64], known)));
  }
  @Test public void contourLibraryCanBeRecalledWithoutChangingVisiblePixels() {
    var examples = TernaryContours.memories(); assertEquals(8, examples.size());
    int[][] rows = examples.stream().map(TernaryAssociativeMemory.Cue::values).toArray(int[][]::new);
    var memory = TernaryAssociativeMemory.train(rows, 10);
    var cue = examples.get(0); boolean[] known = cue.known(); known[9] = false;
    var result = memory.recall(new TernaryAssociativeMemory.Cue(cue.values(), known), 0);
    assertEquals(TernaryAssociativeMemory.format(cue.values()), result.nearest().get(0).pattern());
    for (var candidate : result.spin()) {
      int[] values = TernaryAssociativeMemory.parseCue(candidate.pattern()).values();
      for (int i = 0; i < 64; i++) if (known[i]) assertEquals(cue.values()[i], values[i]);
    }
  }
}
