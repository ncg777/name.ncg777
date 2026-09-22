package name.ncg777.maths.neural;

import static org.junit.Assert.*;
import java.util.*;
import java.util.concurrent.CancellationException;
import org.junit.Test;
import name.ncg777.maths.numbers.*;
import name.ncg777.maths.numbers.predicates.ShadowContourIsomorphic;
import name.ncg777.maths.sequences.Sequence;

public class RhythmContourEmbeddingTests {
  @Test public void usesTheExistingContourAndShadowDefinitions() {
    var rhythm = new Natural(Cipher.Name.Hexadecimal, "8080").toBinaryNatural();
    var example = RhythmContourEmbeddings.example("8080");
    assertTrue(new ShadowContourIsomorphic().apply(rhythm));
    assertEquals(rhythm.getContour().toString(), example.contour()); assertEquals(rhythm.getShadowContour().toString(), example.shadow());
    Sequence path = rhythm.getContour().circularHoldNonZero().cyclicalAntidifference(0).asOrdinalsUnipolar().addToEach(-1);
    assertEquals(path.toString(), example.path()); assertArrayEquals(RhythmContourEmbeddings.rasterize(path), example.pixels());
    int[] copy = example.pixels(); copy[0] = 9; assertNotEquals(9, example.pixels()[0]);
  }
  @Test public void rasterOrientationAndInputBoundsAreExplicit() {
    int[] pixels = RhythmContourEmbeddings.rasterize(Sequence.parse("0 1"));
    for (int x = 0; x < 8; x++) assertEquals(1, pixels[(7 - x) * 8 + x]);
    assertEquals(1, RhythmContourEmbeddings.rasterize(Sequence.parse("0"))[4 * 8 + 3]);
    assertThrows(IllegalArgumentException.class, () -> RhythmContourEmbeddings.example("0000"));
    assertThrows(IllegalArgumentException.class, () -> RhythmContourEmbeddings.example("GGGG"));
    assertThrows(IllegalArgumentException.class, () -> RhythmContourEmbeddings.rasterize(new Sequence()));
    Thread.currentThread().interrupt();
    try { assertThrows(CancellationException.class, () -> RhythmContourEmbeddings.catalogue(null)); }
    finally { Thread.interrupted(); }
  }
  @Test public void catalogueGroupsIdenticalDrawingsBeforeSplitting() {
    var catalogue = RhythmContourEmbeddings.catalogue(null);
    Set<String> drawings = new HashSet<>();
    var all = new ArrayList<>(catalogue.training()); all.addAll(catalogue.testing());
    for (var example : all) {
      assertTrue(drawings.add(Arrays.toString(example.pixels())));
      assertTrue(example.equivalentDrawings() > 0);
      assertTrue(new ShadowContourIsomorphic().apply(new Natural(Cipher.Name.Hexadecimal, example.hex()).toBinaryNatural()));
    }
    assertTrue(catalogue.acceptedPatterns() >= catalogue.distinctDrawings()); assertTrue(catalogue.testing().size() > 0);
    System.out.println("SCI rhythms: " + catalogue.acceptedPatterns() + "; drawings: " + catalogue.distinctDrawings() + "; split: " + catalogue.training().size() + "/" + catalogue.testing().size());
  }
}
