package name.ncg777.maths.lattices;

import static org.junit.Assert.*;
import java.util.Random;
import org.junit.Test;

public class BooleanLatticeTests {
  @Test
  public void agreesWithDirectSubsetSumsAndInverts() {
    var random = new Random(7);
    for (int n = 0; n <= 8; n++) {
      double[] values = new double[1 << n];
      for (int i = 0; i < values.length; i++) values[i] = random.nextInt(21) - 10;
      double[] original = values.clone();
      double[] transformed = BooleanLattice.zetaTransform(values);
      for (int mask = 0; mask < values.length; mask++) {
        double sum = 0;
        for (int subset = 0; subset < values.length; subset++) {
          if ((subset & mask) == subset) sum += values[subset];
        }
        assertEquals(sum, transformed[mask], 0);
      }
      assertArrayEquals(original, values, 0);
      assertArrayEquals(values, BooleanLattice.mobiusTransform(transformed), 0);
    }
  }

  @Test
  public void rejectsInvalidDimensionsAndOverflow() {
    for (double[] values : new double[][] {{}, {1, 2, 3}, {Double.NaN}, {Double.POSITIVE_INFINITY}}) {
      assertThrows(IllegalArgumentException.class, () -> BooleanLattice.zetaTransform(values));
      assertThrows(IllegalArgumentException.class, () -> BooleanLattice.mobiusTransform(values));
    }
    assertThrows(ArithmeticException.class,
        () -> BooleanLattice.zetaTransform(new double[] {Double.MAX_VALUE, Double.MAX_VALUE}));
  }
}
