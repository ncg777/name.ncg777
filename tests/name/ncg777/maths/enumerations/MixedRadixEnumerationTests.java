package name.ncg777.maths.enumerations;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import name.ncg777.maths.relations.FiniteHomoRelation;
import name.ncg777.maths.sequences.Sequence;

public class MixedRadixEnumerationTests {

  @Test
  public final void testBase() {
    int[] base = {2,3,4};
    long n = 2*3*4;
    var mre = new MixedRadixEnumeration(base);
    for(long i=0;i<n;i++) {
      var elem = mre.nextElement();
      var i2c = MixedRadixEnumeration.mapIndexToCoordinates(i, base);
      assertArrayEquals(elem, i2c);
      
      var c2i = MixedRadixEnumeration.mapCoordinatesToIndex(i2c, base);
      assertEquals(i, c2i);
    }
    assertFalse(mre.hasMoreElements());
  }
  @Test
  public void testNeighborRelationBasicCase() {
      // Input points
      List<int[]> points = Arrays.asList(
          new int[]{1, 2, 3},
          new int[]{2, 2, 3},
          new int[]{1, 3, 3},
          new int[]{1, 2, 4}
      );

      // Call the function
      FiniteHomoRelation<Sequence> relation = MixedRadixEnumeration.getNeighborRelation(points);

      // Assertions
      Sequence seq1 = new Sequence(new int[]{1, 2, 3});
      Sequence seq2 = new Sequence(new int[]{2, 2, 3});
      Sequence seq3 = new Sequence(new int[]{1, 3, 3});
      Sequence seq4 = new Sequence(new int[]{1, 2, 4});

      assertTrue(relation.test(seq1, seq2));
      assertTrue(relation.test(seq1, seq3));
      assertTrue(relation.test(seq1, seq4));
      assertFalse(relation.test(seq2, seq3)); // No direct neighbors
  }

  @Test
  public void testNeighborRelationEmptyInput() {
      // Input points
      List<int[]> points = Arrays.asList();

      // Call the function
      FiniteHomoRelation<Sequence> relation = MixedRadixEnumeration.getNeighborRelation(points);

      // Assertions
      assertTrue(relation.isEmpty());
  }

  @Test
  public void testNeighborRelationSinglePoint() {
      // Input points
      List<int[]> points = Arrays.asList(new int[]{1, 1, 1});

      // Call the function
      FiniteHomoRelation<Sequence> relation = MixedRadixEnumeration.getNeighborRelation(points);

      // Assertions
      assertTrue(relation.isEmpty());
  }

  @Test
  public void testNeighborRelationMultipleNeighbors() {
      // Input points
      List<int[]> points = Arrays.asList(
          new int[]{1, 1, 1},
          new int[]{2, 1, 1},
          new int[]{1, 2, 1},
          new int[]{1, 1, 2}
      );

      // Call the function
      FiniteHomoRelation<Sequence> relation = MixedRadixEnumeration.getNeighborRelation(points);

      // Assertions
      Sequence seq1 = new Sequence(new int[]{1, 1, 1});
      Sequence seq2 = new Sequence(new int[]{2, 1, 1});
      Sequence seq3 = new Sequence(new int[]{1, 2, 1});
      Sequence seq4 = new Sequence(new int[]{1, 1, 2});

      assertTrue(relation.test(seq1, seq2));
      assertTrue(relation.test(seq1, seq3));
      assertTrue(relation.test(seq1, seq4));
  }
}
