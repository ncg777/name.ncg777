package name.ncg777.computing.structures;

import static org.junit.Assert.*;

import org.junit.Test;

import name.ncg777.maths.sequences.Sequence;
import name.ncg777.statistics.RandomNumberGenerator;

public class CollectionUtilsTests {

  @Test
  public void testLehmerCodes() {
      int n=100;
      for(int i=0;i<n;i++) {
        int p = RandomNumberGenerator.nextInt(1000000);
        var pm = new Sequence(CollectionUtils.getPermutation(p));
        var pn = CollectionUtils.getPermutationNumber(pm);
        assertEquals(pn,p);
      }
  }

}
