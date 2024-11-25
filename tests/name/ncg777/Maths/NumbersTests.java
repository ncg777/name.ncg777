package name.ncg777.Maths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
  @Test
  public void testBinomialValidInputs() {
      // Basic cases
      assertEquals(1, Numbers.binomial(0, 0));
      assertEquals(1, Numbers.binomial(5, 0));
      assertEquals(5, Numbers.binomial(5, 1));
      assertEquals(10, Numbers.binomial(5, 2));
      assertEquals(252, Numbers.binomial(10, 5));

      // Symmetry property: binomial(n, k) == binomial(n, n-k)
      assertEquals(Numbers.binomial(10, 3), Numbers.binomial(10, 7));
      assertEquals(Numbers.binomial(20, 6), Numbers.binomial(20, 14));
  }

  @Test
  public void testBinomialInvalidNegativeN() {
      try {
          Numbers.binomial(-1, 1);
          fail("Expected IllegalArgumentException for negative n");
      } catch (IllegalArgumentException e) {
          assertEquals("n must be non-negative.", e.getMessage());
      }
  }

  @Test
  public void testBinomialInvalidNegativeK() {
      try {
          Numbers.binomial(5, -1);
          fail("Expected IllegalArgumentException for negative k");
      } catch (IllegalArgumentException e) {
          assertEquals("k must be non-negative.", e.getMessage());
      }
  }

  @Test
  public void testBinomialInvalidKGreaterThanN() {
      try {
          Numbers.binomial(5, 6);
          fail("Expected IllegalArgumentException for k > n");
      } catch (IllegalArgumentException e) {
          assertEquals("k cannot be greater than n.", e.getMessage());
      }
  }

  @Test
  public void testBinomialOverflow() {
      try {
          Numbers.binomial(100, 50); // This should cause overflow for long
          fail("Expected ArithmeticException for overflow");
      } catch (ArithmeticException e) {
          assertEquals("Overflow detected.", e.getMessage());
      }
  }
}
