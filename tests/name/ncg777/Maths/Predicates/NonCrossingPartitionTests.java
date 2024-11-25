package name.ncg777.Maths.Predicates;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import name.ncg777.Maths.Numbers;
import name.ncg777.Maths.Enumerations.SetPartitionEnumeration;
import name.ncg777.Maths.Objects.Sequence;

public class NonCrossingPartitionTests extends TestCase  {

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public final void testCatalanBell() {
    NonCrossingPartition pred = new NonCrossingPartition();
    for(int i=0;i<9;i++) {
      long nc = Numbers.catalan(i);
      long c = Numbers.bell(i)-nc;
      
      int cntnc = 0;
      int cntc = 0;
      
      SetPartitionEnumeration e = new SetPartitionEnumeration(i);
      
      while(e.hasMoreElements()) {
        var el = e.nextElement();
        int[] el1 = new int[el.length];
        for(int j=0;j<el.length;j++) el1[j] = el[j];
        System.out.println(new Sequence(el));
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
