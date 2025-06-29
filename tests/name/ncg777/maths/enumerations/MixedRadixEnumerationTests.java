package name.ncg777.maths.enumerations;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import name.ncg777.computing.structures.ImmutableIntArray;
import name.ncg777.maths.relations.FiniteHomoRelation;

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
}
