package name.NicolasCoutureGrenier.CS.DataStructures;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class TritChainTests {

  @Test
  public void testSetAndGet() {
      TritChain tritChain = new TritChain(32);

      tritChain.set(0, -1);
      tritChain.set(1, 0);
      tritChain.set(2, 1);
      tritChain.set(3, -1);

      assertEquals(-1, tritChain.get(0));
      assertEquals(0, tritChain.get(1));
      assertEquals(1, tritChain.get(2));
      assertEquals(-1, tritChain.get(3));
  }

  @Test
  public void testQuantize() {
      Iterable<Double> data = Arrays.asList(-1.5, -0.5, 0.0, 0.5, 1.5);
      double threshold = 1.0;

      TritChain tritChain = TritChain.quantize(data, threshold);

      assertEquals(-1, tritChain.get(0));
      assertEquals(0, tritChain.get(1));
      assertEquals(0, tritChain.get(2));
      assertEquals(0, tritChain.get(3));
      assertEquals(1, tritChain.get(4));
  }

  @Test
  public void testLargeTritSet() {
      int size = 1000;
      TritChain tritChain = new TritChain(size);

      for (int i = 0; i < size; i++) {
          tritChain.set(i, i % 3 - 1);  // Cycles through -1, 0, 1
      }

      for (int i = 0; i < size; i++) {
          assertEquals(i % 3 - 1, tritChain.get(i));
      }
  }

  @Test
  public void testNegativeThreshold() {
      Iterable<Double> data = Arrays.asList(-1.5, -2.0, 1.0, 2.5, -0.2);
      double threshold = 1.0;

      TritChain tritChain = TritChain.quantize(data, threshold);

      assertEquals(-1, tritChain.get(0));
      assertEquals(-1, tritChain.get(1));
      assertEquals(0, tritChain.get(2));
      assertEquals(1, tritChain.get(3));
      assertEquals(0, tritChain.get(4));
  }
}
