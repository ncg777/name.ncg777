package name.NicolasCoutureGrenier.Maths.Enumerations;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Efficient Generation of Set Partitions Michael Orlov orlovm@cs.bgu.ac.il March 26, 2002
 * 
 * @author Nicolas Couture-Grenier
 * 
 */
public class FixedSetPartitionEnumeration implements Enumeration<Integer[]> {
  private Integer[] kappa;
  private Integer[] M;
  private int n;
  private int k;
  private boolean hasNext = true;



  public FixedSetPartitionEnumeration(int n, int k) {
    this.n = n;
    this.k = k;
    this.kappa = new Integer[n];
    Arrays.fill(kappa, 0);
    this.M = new Integer[n];
    Arrays.fill(M, 0);

    int offset = n - k;
    for (int i = offset + 1; i < n; i++) {
      kappa[i] = i - offset;
      M[i] = i - offset;
    }
  }


  @Override
  public Integer[] nextElement() {
    if (!hasNext) {
      throw new NoSuchElementException();
    }
    Integer[] o = Arrays.copyOf(kappa, kappa.length);
    
    hasNext = false;

    for (int i = n - 1; i > 0; --i) {
      if (kappa[i] < k - 1 && kappa[i] <= M[i - 1]) {
        kappa[i] = kappa[i] + 1;

        int new_max = Math.max(M[i], kappa[i]);
        M[i] = new_max;

        for (int j = i + 1; j <= n - (k - new_max); ++j) {
          kappa[j] = 0;
          M[j] = new_max;
        }

        for (int j = n - (k - new_max) + 1; j < n; ++j) {
          int new_max2 = k - (n - j);
          kappa[j] = new_max2;
          M[j] = new_max2;
        }

        hasNext = true;
        break;
      }
    }
    return o;
  }


  @Override
  public boolean hasMoreElements() {
    return hasNext;
  }

}
