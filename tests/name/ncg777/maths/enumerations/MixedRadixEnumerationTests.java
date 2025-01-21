package name.ncg777.maths.enumerations;

import static org.junit.Assert.*;

import org.junit.Test;

public class MixedRadixEnumerationTests {

  @Test
  public final void testBase() {
    int[] base = {2,3,4};
    int n = 2*3*4;
    var mre = new MixedRadixEnumeration(base);
    for(int i=0;i<n;i++) {
      var elem = mre.nextElement();
      var i2c = MixedRadixEnumeration.mapIndexToCoordinates(i, base);
      assertArrayEquals(elem, i2c);
      
      var c2i = MixedRadixEnumeration.mapCoordinatesToIndex(i2c, base);
      assertEquals(i, c2i);
    }
    assertFalse(mre.hasMoreElements());
  }

}
