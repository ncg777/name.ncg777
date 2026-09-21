package name.ncg777.maths.neural;

import static org.junit.Assert.*;
import java.util.List;
import java.util.concurrent.CancellationException;
import org.junit.Test;
import name.ncg777.maths.physics.TernarySpinModel;

public class TernaryAssociativeMemoryTests {
  private TernaryAssociativeMemory uniform(String patterns) {
    int[][] rows = TernaryAssociativeMemory.parsePatterns(patterns); int n = rows[0].length;
    return TernaryAssociativeMemory.fromModel(rows, new TernarySpinModel(new double[n][n], new double[n], new double[n]), 1);
  }
  @Test public void neutralIsObservedWhileQuestionMarkIsMissing() {
    var memory = uniform("T1\n11");
    var recall = memory.recall(TernaryAssociativeMemory.parseCue("0?"), 0);
    assertEquals(3, recall.spin().size());
    for (var candidate : recall.spin()) {
      assertTrue(candidate.pattern().startsWith("0")); assertEquals(1.0 / 3, candidate.weight(), 1e-12);
      assertFalse(candidate.stored());
    }
    assertEquals(9, memory.recall(TernaryAssociativeMemory.parseCue("??"), 0).spin().size());
    assertEquals(1, memory.recall(TernaryAssociativeMemory.parseCue("01"), 0).spin().size());
  }
  @Test public void noisyEvidenceProducesAnalyticalPosterior() {
    var recall = uniform("T\n0\n1").recall(TernaryAssociativeMemory.parseCue("1"), 0.2);
    assertEquals("1", recall.spin().get(0).pattern());
    assertEquals(0.8, recall.spin().get(0).weight(), 1e-12);
    assertEquals(0.1, recall.spin().get(1).weight(), 1e-12);
    assertEquals(1, recall.spin().stream().mapToDouble(c -> c.weight()).sum(), 1e-12);
  }
  @Test public void nearestUsesKnownHammingDistanceAndExampleFrequency() {
    var recall = uniform("T1\nT1\n11\n00").recall(TernaryAssociativeMemory.parseCue("?1"), 0);
    assertEquals(2, recall.nearest().size());
    assertEquals("T1", recall.nearest().get(0).pattern());
    assertEquals(2.0 / 3, recall.nearest().get(0).weight(), 1e-12);
    assertEquals(0, recall.nearest().get(0).mismatches());
    var unmatched = uniform("TT\n11").recall(TernaryAssociativeMemory.parseCue("00"), 0);
    assertEquals(2, unmatched.nearest().get(0).mismatches());
    assertEquals(0.5, unmatched.nearest().get(0).weight(), 0);
  }
  @Test public void fittedMemoryRecoversSimpleAssociation() {
    var memory = TernaryAssociativeMemory.train(TernaryAssociativeMemory.parsePatterns("11\nTT"), 200);
    assertEquals("11", memory.recall(TernaryAssociativeMemory.parseCue("1?"), 0).spin().get(0).pattern());
    assertEquals("TT", memory.recall(TernaryAssociativeMemory.parseCue("T?"), 0).spin().get(0).pattern());
    var benchmark = memory.benchmark(100, 1, 0, 777);
    assertEquals(100, benchmark.spinCorrect()); assertEquals(100, benchmark.nearestCorrect());
    assertEquals(benchmark, memory.benchmark(100, 1, 0, 777));
    System.out.println("Associative recall sanity check: " + benchmark);
  }
  @Test public void cueAndStoredDataAreDefensivelyCopied() {
    int[] values = {1}; boolean[] known = {true};
    var cue = new TernaryAssociativeMemory.Cue(values, known);
    values[0] = -1; known[0] = false; cue.values()[0] = 0; cue.known()[0] = false;
    assertEquals("1", uniform("1").recall(cue, 0).spin().get(0).pattern());
    int[][] rows = {{1}};
    var memory = TernaryAssociativeMemory.fromModel(rows, new TernarySpinModel(new double[1][1], new double[1], new double[1]), 1);
    rows[0][0] = -1;
    assertEquals("1", memory.recall(TernaryAssociativeMemory.parseCue("?"), 0).nearest().get(0).pattern());
  }
  @Test public void validatesFormatsAndLimits() {
    assertArrayEquals(new int[]{-1, 0, 1}, TernaryAssociativeMemory.parsePatterns("T 0 1")[0]);
    for (String input : List.of("", "1111111", "T?1", "12", "T1\n0"))
      assertThrows(IllegalArgumentException.class, () -> TernaryAssociativeMemory.parsePatterns(input));
    var memory = uniform("11");
    assertThrows(IllegalArgumentException.class, () -> memory.recall(TernaryAssociativeMemory.parseCue("?"), 0));
    assertThrows(IllegalArgumentException.class, () -> memory.recall(TernaryAssociativeMemory.parseCue("??"), Double.NaN));
    assertThrows(IllegalArgumentException.class, () -> memory.benchmark(501, 1, 0, 1));
    assertThrows(IllegalArgumentException.class, () -> memory.benchmark(10, 3, 0, 1));
    assertThrows(IllegalArgumentException.class, () -> TernaryAssociativeMemory.train(new int[][]{{1}}, 501));
    try {
      Thread.currentThread().interrupt();
      assertThrows(CancellationException.class, () -> memory.benchmark(10, 1, 0, 1));
    } finally { Thread.interrupted(); }
  }
}
