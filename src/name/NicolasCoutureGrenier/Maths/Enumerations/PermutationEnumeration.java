// http://www.merriampark.com/perm.htm

package name.NicolasCoutureGrenier.Maths.Enumerations;

// --------------------------------------
// Systematically generate permutations.
// --------------------------------------

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Enumeration;
/**
 * Taken from http://www.merriampark.com/perm.htm and adapted to Enumeration interface.
 * 
 * @author Nicolas Couture-Grenier
 *
 */
public class PermutationEnumeration  implements Enumeration<Integer[]> {

  private Integer[] a;
  private BigInteger numLeft;
  private BigInteger total;

  // -----------------------------------------------------------
  // Constructor. WARNING: Don't make n too large.
  // Recall that the number of permutations is n!
  // which can be very large, even when n is as small as 20 --
  // 20! = 2,432,902,008,176,640,000 and
  // 21! is too big to fit into a Java long, which is
  // why we use BigInteger instead.
  // ----------------------------------------------------------

  public PermutationEnumeration(int n) {
    if (n < 1) {
      throw new IllegalArgumentException("Min 1");
    }
    a = new Integer[n];
    total = getFactorial(n);
    reset();
  }

  // ------
  // Reset
  // ------

  public void reset() {
    for (int i = 0; i < a.length; i++) {
      a[i] = i;
    }
    numLeft = new BigInteger(total.toString());
  }

  // ------------------------------------------------
  // Return number of permutations not yet generated
  // ------------------------------------------------

  public BigInteger getNumLeft() {
    return numLeft;
  }

  // ------------------------------------
  // Return total number of permutations
  // ------------------------------------

  public BigInteger getTotal() {
    return total;
  }

  // -----------------------------
  // Are there more permutations?
  // -----------------------------

  public boolean hasMore() {
    return numLeft.compareTo(BigInteger.ZERO) == 1;
  }

  // ------------------
  // Compute factorial
  // ------------------

  private static BigInteger getFactorial(int n) {
    BigInteger fact = BigInteger.ONE;
    for (int i = n; i > 1; i--) {
      fact = fact.multiply(new BigInteger(Integer.toString(i)));
    }
    return fact;
  }

  // --------------------------------------------------------
  // Generate next permutation (algorithm from Rosen p. 284)
  // --------------------------------------------------------

  public static Integer[] getNext(Integer[] a0) {
    Integer[] a = Arrays.copyOf(a0, a0.length);
    
    int temp;

    // Find largest index j with a[j] < a[j+1]

    int j = a.length - 2;
    try{
      while (a[j] > a[j + 1]) {
        j--;
      }
    } catch(IndexOutOfBoundsException ex){
      return null;
    }
    // Find index k such that a[k] is smallest integer
    // greater than a[j] to the right of a[j]

    int k = a.length - 1;
    while (a[j] > a[k]) {
      k--;
    }

    // Interchange a[j] and a[k]

    temp = a[k];
    a[k] = a[j];
    a[j] = temp;

    // Put tail end of permutation after jth position in increasing order

    int r = a.length - 1;
    int s = j + 1;

    while (r > s) {
      temp = a[s];
      a[s] = a[r];
      a[r] = temp;
      r--;
      s++;
    }
    return a;
  }
  
  public Integer[] getNext() {

    if (numLeft.equals(total)) {
      numLeft = numLeft.subtract(BigInteger.ONE);
      return a;
    }

    a = getNext(a);

    numLeft = numLeft.subtract(BigInteger.ONE);
    return a;

  }
  @Override
  public boolean hasMoreElements() {
    return this.hasMore();
  }
  
  @Override
  public Integer[] nextElement() {
    return this.getNext();
  }
}
