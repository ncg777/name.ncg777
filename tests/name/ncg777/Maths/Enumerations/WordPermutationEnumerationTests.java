package name.ncg777.Maths.Enumerations;

import java.util.Arrays;

import junit.framework.TestCase;
import name.ncg777.Maths.Objects.Sequence;

public class WordPermutationEnumerationTests extends TestCase {
  
  public WordPermutationEnumerationTests(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    
  }
  
  public final void testNextElement() {
    int[] rk = {1,2,3};
    
    var wpe = new WordPermutationEnumeration(rk);
    int[] e1 = {2,2,2,1,1,0};
    var we1 = wpe.nextElement();
    assertTrue(Arrays.equals(we1, e1));
    int[] e2 = {2,2,2,1,0,1};
    var we2 = wpe.nextElement();
    System.out.println(new Sequence(we2));
    assertTrue(Arrays.equals(we2, e2));
  }
  
  public final void testCount() {
    int[] rk = {1,2,3};
    
    var wpe = new WordPermutationEnumeration(rk);
    int count = 0;
    while(wpe.hasMoreElements()) {wpe.nextElement(); count++;}
    assertEquals(count, 60);
  }
}