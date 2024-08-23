package name.NicolasCoutureGrenier.Maths.Enumerations;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import name.NicolasCoutureGrenier.Maths.Numbers;

public class SetPartitionEnumerationTest extends TestCase {

  @Before
  protected void setUp() throws Exception {
    super.setUp();
  }

  @After
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  @Test
  public final void testSetPartitionEnumeration() {
    for(int i=0;i<8;i++) {
      
      int b = Numbers.bell(i);
      
      int c = 0;
      var e = new SetPartitionEnumeration(i);
      while(e.hasMoreElements()) {
        e.nextElement(); 
        c++;
        
      }
      
      assertEquals(b, c);
    }
  }

}
