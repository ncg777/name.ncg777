package name.ncg777.maths.neural;

import static org.junit.Assert.*;
import java.util.*;
import java.util.concurrent.CancellationException;
import org.junit.Test;
import name.ncg777.maths.numbers.*;
import name.ncg777.maths.numbers.predicates.ShadowContourIsomorphic;
import name.ncg777.maths.numbers.relations.PredicatedDifferences;

public class RhythmCompletionTests {
  private static final ValidRhythmDecoder DECODER = new ValidRhythmDecoder();
  @Test public void bulkCompletionKeepsMasksAndAllPairwiseRelations() {
    var masks = List.of("8?8?", "???????????????1", "????", "????");
    var result = DECODER.complete(masks, 1000, 100000, 777);
    assertEquals(1000, result.stacks().size()); assertTrue(result.resultLimitReached());
    Set<List<String>> unique = new HashSet<>();
    var relation = new PredicatedDifferences(new ShadowContourIsomorphic());
    for (var stack : result.stacks()) {
      assertTrue(unique.add(stack.hex())); assertTrue(stack.hex().get(0).matches("8.8."));
      assertEquals(1, Integer.parseInt(stack.hex().get(1), 16) & 1);
      for (int row = 0; row < 4; row++) {
        RhythmContourEmbeddings.example(stack.hex().get(row));
        for (int previous = 0; previous < row; previous++) assertTrue(relation.test(
          new Natural(Cipher.Name.Hexadecimal, stack.hex().get(row)).toBinaryNatural(),
          new Natural(Cipher.Name.Hexadecimal, stack.hex().get(previous)).toBinaryNatural()));
      }
    }
    assertEquals(result.stacks().stream().map(RhythmContourStacks.Stack::hex).toList(), DECODER.complete(masks, 1000, 100000, 777).stacks().stream().map(RhythmContourStacks.Stack::hex).toList());
  }
  @Test public void fixedBarsNoSolutionAndBudgetAreDistinguished() {
    var fixed = DECODER.complete(List.of("8080", "8080", "8080", "8080"), 16, 100000, 777);
    assertEquals(1, fixed.stacks().size()); assertFalse(fixed.searchLimitReached()); assertFalse(fixed.resultLimitReached());
    var impossible = DECODER.complete(List.of("0000", "????", "????", "????"), 16, 100000, 777);
    assertTrue(impossible.stacks().isEmpty()); assertFalse(impossible.searchLimitReached());
    var bounded = DECODER.complete(List.of("????", "????", "????", "????"), 16, 1, 777);
    assertTrue(bounded.searchLimitReached()); assertEquals(1, bounded.attempts());
  }
  @Test public void invalidMasksAndCancellation() {
    assertThrows(IllegalArgumentException.class, () -> DECODER.complete(List.of("????"), 16, 100, 777));
    assertThrows(IllegalArgumentException.class, () -> DECODER.complete(List.of("GGGG", "????", "????", "????"), 16, 100, 777));
    Thread.currentThread().interrupt();
    try { assertThrows(CancellationException.class, () -> DECODER.complete(List.of("????", "????", "????", "????"), 16, 100, 777)); }
    finally { Thread.interrupted(); }
  }
}
