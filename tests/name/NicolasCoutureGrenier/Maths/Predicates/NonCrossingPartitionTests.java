package name.NicolasCoutureGrenier.Maths.Predicates;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import name.NicolasCoutureGrenier.Maths.Numbers;
import name.NicolasCoutureGrenier.Maths.Enumerations.NonCrossingPartitionEnumeration;
import name.NicolasCoutureGrenier.Maths.Enumerations.SetPartitionEnumeration;

public class NonCrossingPartitionTests extends TestCase  {

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public final void testCatalanBell() {
    int[] n = {4,5,6,7};
    NonCrossingPartition pred = new NonCrossingPartition();
    for(int i=0;i<n.length;i++) {
      int nc = Numbers.catalan(n[i]);
      int c = Numbers.bell(n[i])-nc;
      
      int cntnc = 0;
      int cntc = 0;
      
      SetPartitionEnumeration e = new SetPartitionEnumeration(n[i]);
      
      while(e.hasMoreElements()) {
        var el = e.nextElement();
        int[] el1 = new int[el.length];
        for(int j=0;j<el.length;j++) el1[i] = el[i];
        if(pred.test(el1)) {
          cntnc++;
        } else {
          cntc++;
        }
      }

      assertEquals(c, cntc);
      assertEquals(nc, cntnc);
    }
    
    
  }

}
