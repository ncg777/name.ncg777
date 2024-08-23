package name.NicolasCoutureGrenier.Maths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;

public class NumbersTests extends TestCase {

  @Before
  protected void setUp() throws Exception {
    super.setUp();
  }

  @After
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  @Test
  public final void testCatalan() {
    int[] vals = {1, 1, 2, 5, 14, 42, 132, 429, 1430, 4862, 16796};
    for(int i=0;i<vals.length;i++) {
      assertEquals(Numbers.catalan(i), vals[i]);  
    }
  }

  @Test
  public final void testBell() {
    int[] vals = {1, 1, 2, 5, 15, 52, 203, 877, 4140, 21147, 115975};
    for(int i=0;i<vals.length;i++) {
      assertEquals(vals[i], Numbers.bell(i));  
    }}

}
