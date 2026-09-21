package name.ncg777.maths.neural;

import static org.junit.Assert.*;
import java.util.*;
import java.util.concurrent.CancellationException;
import org.junit.Test;

public class TernaryAutoencoderTests {
  @Test public void splitIsDisjointAndAllBottlenecksAreTrulyTernary() {
    var data = TernaryEmbeddingExperiment.dataset(); Set<String> unique = new HashSet<>();
    for (int[] row : data.training()) assertTrue(unique.add(Arrays.toString(row)));
    for (int[] row : data.testing()) assertTrue(unique.add(Arrays.toString(row)));
    assertEquals(256, unique.size());
    var fit = TernaryEmbeddingExperiment.train(data, null); assertEquals(14, fit.rows().size());
    for (var entry : fit.models().entrySet()) {
      var model = entry.getValue(); int[] input = data.testing()[0], code = model.encode(input);
      assertEquals((int) entry.getKey(), code.length);
      for (int trit : code) assertTrue(trit >= -1 && trit <= 1);
      assertArrayEquals(model.reconstruct(input), TernaryAutoencoder.binary(model.decode(code)));
      double[] decoded = model.decode(code); Arrays.fill(input, 0);
      assertArrayEquals(decoded, model.decode(code), 0); // Decoder has no source-pixel bypass.
      for (double p : decoded) assertTrue(Double.isFinite(p) && p >= 0 && p <= 1);
    }
    for (var row : fit.rows()) {
      System.out.printf(Locale.ROOT, "%s / %s: error %.4f F1 %.4f exact %.4f code change %.4f%n", row.method(), row.input(), row.score().errorRate(), row.score().f1(), row.score().exactRate(), row.codeChange());
      assertTrue(row.score().errorRate() >= 0 && row.score().errorRate() <= 1);
    }
    assertEquals(0, fit.rows().get(0).score().errorRate(), 0);
    assertEquals(1, fit.rows().get(0).score().exactRate(), 0);
    assertTrue(fit.rows().get(6).score().errorRate() < fit.rows().get(1).score().errorRate());
  }
  @Test public void aTinyAssociationCanBeLearnedAndTrainingIsDeterministic() {
    int[] blank = new int[64], filled = new int[64]; Arrays.fill(filled, 1);
    int[][] rows = {blank, filled};
    var a = TernaryAutoencoder.train(rows, 8, 200, 0, 5, null);
    var b = TernaryAutoencoder.train(rows, 8, 200, 0, 5, null);
    assertArrayEquals(a.encode(filled), b.encode(filled)); assertArrayEquals(a.decode(a.encode(filled)), b.decode(b.encode(filled)), 0);
    assertArrayEquals(filled, a.reconstruct(filled)); assertArrayEquals(blank, a.reconstruct(blank));
  }
  @Test public void limitsInvalidInputsCorruptionAndCancellation() {
    int[][] rows = {new int[64]};
    assertThrows(IllegalArgumentException.class, () -> TernaryAutoencoder.train(rows, 7, 1, 0, 0, null));
    assertThrows(IllegalArgumentException.class, () -> TernaryAutoencoder.train(rows, 8, 1, Double.NaN, 0, null));
    assertThrows(IllegalArgumentException.class, () -> TernaryAutoencoder.train(new int[1024][64], 64, 200, 0, 0, null));
    var model = TernaryAutoencoder.train(rows, 8, 1, 0, 0, null);
    assertThrows(IllegalArgumentException.class, () -> model.decode(new int[9]));
    assertThrows(IllegalArgumentException.class, () -> model.decode(new int[] {2,0,0,0,0,0,0,0}));
    assertThrows(IllegalArgumentException.class, () -> model.encode(new int[63]));
    assertArrayEquals(rows[0], TernaryAutoencoder.corrupt(rows[0], 0, 1));
    assertArrayEquals(TernaryAutoencoder.corrupt(rows[0], .5, 1), TernaryAutoencoder.corrupt(rows[0], .5, 1));
    Thread.currentThread().interrupt();
    try { assertThrows(CancellationException.class, () -> TernaryAutoencoder.train(rows, 8, 1, 0, 0, null)); }
    finally { Thread.interrupted(); }
  }
}
