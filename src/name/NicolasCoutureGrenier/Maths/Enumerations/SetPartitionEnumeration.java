package name.NicolasCoutureGrenier.Maths.Enumerations;

import java.util.Arrays;
import java.util.Enumeration;

/**
 * Efficient Generation of Set Partitions Michael Orlov orlovm@cs.bgu.ac.il March 26, 2002
 * 
 */
public class SetPartitionEnumeration implements Enumeration<Integer[]> {
  private Integer[] kappa;
  private Integer[] M;
  private Integer n;
  private boolean hasNext = true;

  public SetPartitionEnumeration(Integer n) {
    this.n = n;
    this.kappa = new Integer[n];
    Arrays.fill(kappa, 0);
    this.M = new Integer[n];
    Arrays.fill(M, 0);
  }

  @Override
  public Integer[] nextElement() {
    Integer[] o = Arrays.copyOf(kappa, kappa.length);
    if (!hasNext) {
      return o;
    }
    hasNext = false;
    for (Integer i = n - 1; i > 0; --i) {
      if (kappa[i] <= M[i - 1]) {
        kappa[i] = kappa[i] + 1;

        Integer new_max = Math.max(M[i], kappa[i]);
        M[i] = new_max;
        for (Integer j = i + 1; j < n; ++j) {
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
