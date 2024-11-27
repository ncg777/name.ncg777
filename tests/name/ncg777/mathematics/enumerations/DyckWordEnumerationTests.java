package name.ncg777.mathematics.enumerations;

import junit.framework.TestCase;
import name.ncg777.mathematics.Numbers;

public class DyckWordEnumerationTests extends TestCase {

  public DyckWordEnumerationTests(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    
  }
  
  public void testDyckWordCatalanNumber() {
      for (int n = 0; n <= 8; n++) {
          var enumerator = new DyckWordEnumeration(n);
          int count = 0;
          while (enumerator.hasMoreElements()) {
              enumerator.nextElement();
              count++;
          }
          assertEquals(Numbers.catalan(n), count);
      }
  }
}
