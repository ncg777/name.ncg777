package name.ncg777.maths.neural;

import static org.junit.Assert.*;
import java.util.*;
import java.util.concurrent.CancellationException;
import org.junit.Test;
import name.ncg777.maths.numbers.*;
import name.ncg777.maths.numbers.predicates.ShadowContourIsomorphic;
import name.ncg777.maths.numbers.relations.PredicatedDifferences;

public class ValidRhythmDecoderTests {
  private static final ValidRhythmDecoder DECODER = new ValidRhythmDecoder();
  @Test public void arbitraryCurvesProduceCompatibleBarsAndTheirActualContours() {
    Random random = new Random(777);
    var difference = new PredicatedDifferences(new ShadowContourIsomorphic());
    for (int trial = 0; trial < 20; trial++) {
      double[] input = new double[64]; for (int i = 0; i < 64; i++) input[i] = random.nextDouble() * 2 - 1;
      double[] copy = input.clone(); var result = DECODER.decode(input);
      assertArrayEquals(copy, input, 0); assertEquals(result.hex(), DECODER.decode(input).hex());
      for (int row = 0; row < 4; row++) {
        var example = RhythmContourEmbeddings.example(result.hex().get(row));
        assertArrayEquals(RhythmContourStacks.samples(example), Arrays.copyOfRange(result.values(), row * 16, row * 16 + 16), 0);
        for (int previous = 0; previous < row; previous++) assertTrue(difference.test(
            new Natural(Cipher.Name.Hexadecimal, result.hex().get(row)).toBinaryNatural(),
            new Natural(Cipher.Name.Hexadecimal, result.hex().get(previous)).toBinaryNatural()));
      }
    }
  }
  @Test public void flatContourHasExactValidInterpretation() {
    var result = DECODER.decode(new double[64]);
    assertArrayEquals(new double[64], result.values(), 0);
    assertEquals(List.of("0001", "0001", "0001", "0001"), result.hex());
  }
  @Test public void rejectsInvalidInputsAndHonoursCancellation() {
    assertThrows(IllegalArgumentException.class, () -> DECODER.decode(new double[63]));
    assertThrows(IllegalArgumentException.class, () -> DECODER.decode(null));
    for (double invalid : new double[] {Double.NaN, Double.POSITIVE_INFINITY, 1.1, -1.1}) {
      double[] input = new double[64]; input[0] = invalid;
      assertThrows(IllegalArgumentException.class, () -> DECODER.decode(input));
    }
    Thread.currentThread().interrupt();
    try { assertThrows(CancellationException.class, () -> DECODER.decode(new double[64])); }
    finally { Thread.interrupted(); }
  }
}
