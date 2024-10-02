package name.NicolasCoutureGrenier.CS.DataStructures;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class TritSetTests {

  @Test
  public void testSetAndGet() {
      TritSet tritSet = new TritSet(32);

      tritSet.set(0, -1);
      tritSet.set(1, 0);
      tritSet.set(2, 1);
      tritSet.set(3, -1);

      assertEquals(-1, tritSet.get(0));
      assertEquals(0, tritSet.get(1));
      assertEquals(1, tritSet.get(2));
      assertEquals(-1, tritSet.get(3));
  }

  @Test
  public void testQuantize() {
      Iterable<Double> data = Arrays.asList(-1.5, -0.5, 0.0, 0.5, 1.5);
      double threshold = 1.0;

      TritSet tritSet = TritSet.quantize(data, threshold);

      assertEquals(-1, tritSet.get(0));
      assertEquals(0, tritSet.get(1));
      assertEquals(0, tritSet.get(2));
      assertEquals(0, tritSet.get(3));
      assertEquals(1, tritSet.get(4));
  }

  @Test
  public void testLargeTritSet() {
      int size = 1000;
      TritSet tritSet = new TritSet(size);

      for (int i = 0; i < size; i++) {
          tritSet.set(i, i % 3 - 1);  // Cycles through -1, 0, 1
      }

      for (int i = 0; i < size; i++) {
          assertEquals(i % 3 - 1, tritSet.get(i));
      }
  }

  @Test
  public void testNegativeThreshold() {
      Iterable<Double> data = Arrays.asList(-1.5, -2.0, 1.0, 2.5, -0.2);
      double threshold = 1.0;

      TritSet tritSet = TritSet.quantize(data, threshold);

      assertEquals(-1, tritSet.get(0));
      assertEquals(-1, tritSet.get(1));
      assertEquals(0, tritSet.get(2));
      assertEquals(1, tritSet.get(3));
      assertEquals(0, tritSet.get(4));
  }
}
