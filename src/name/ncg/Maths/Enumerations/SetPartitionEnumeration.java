package name.ncg.Maths.Enumerations;

import java.util.Arrays;
import java.util.Enumeration;

/**
 * Efficient Generation of Set Partitions Michael Orlov orlovm@cs.bgu.ac.il March 26, 2002
 * 
 */
public class SetPartitionEnumeration implements Enumeration<int[]> {
  private int[] kappa;
  private int[] M;
  private int n;
  private boolean hasNext = true;

  public SetPartitionEnumeration(int n) {
    this.n = n;
    this.kappa = new int[n];
    Arrays.fill(kappa, 0);
    this.M = new int[n];
    Arrays.fill(M, 0);
  }



  @Override
  public int[] nextElement() {
    int[] o = Arrays.copyOf(kappa, kappa.length);
    if (!hasNext) {
      return o;
    }
    hasNext = false;
    for (int i = n - 1; i > 0; --i) {
      if (kappa[i] <= M[i - 1]) {
        kappa[i] = kappa[i] + 1;

        int new_max = Math.max(M[i], kappa[i]);
        M[i] = new_max;
        for (int j = i + 1; j < n; ++j) {
          kappa[j] = 0;
          M[j] = new_max;
        }

        hasNext = true;
      }
    }
    return o;
  }

  @Override
  public boolean hasMoreElements() {
    return hasNext;
  }

}
