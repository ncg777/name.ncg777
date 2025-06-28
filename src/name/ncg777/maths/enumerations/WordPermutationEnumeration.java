package name.ncg777.maths.enumerations;

import java.util.ArrayList;
import java.util.Enumeration;

import name.ncg777.maths.Combination;

/**
 * Generates the combinatorial objects associated with multinomial coefficients
 * in reverse lexicographic order.
 * 
 * @author Nicolas Couture-Grenier
 */
public class WordPermutationEnumeration implements Enumeration<int[]> {
  private ArrayList<Integer> nonzeroindices;
  private Combination[][] combis;
  private Enumeration<int[]> it;
  private Integer n;
  
  /**
   * For example, to enumerate the permutations of MISSISSIPPI, 
   * 
   * rk would be {1,4,4,2} because it has 1 M, 4 Is, 4 Ss, and 2 Ps.
   * 
   * In the enumerated arrays, the 0s represent the M, the 1s the I,
   * the 2s the S and the 3 the Ps.
   * 
   * @param rk Integer[]
   */
  public WordPermutationEnumeration(int[] rk) {
    super();

    if (rk == null) {
      throw new RuntimeException("null array");
    }

    int k = rk.length;

    n = 0;

    nonzeroindices = new ArrayList<Integer>();

    for (int i = 0; i < k; i++) {
      if (rk[i] < 0) {
        throw new RuntimeException("null or negative element");
      }
      n += rk[i];
      if (rk[i] != 0) {
        nonzeroindices.add(i);
      }
    }

    int c = 0;

    int nzsz = nonzeroindices.size();
    combis = new Combination[nzsz][];

    int[] sizes = new int[nzsz];
    for (int i = 0; i < nzsz; i++) {
      int nz = nonzeroindices.get(i);
      c += rk[nz];

      combis[i] = Combination.generate(c, rk[nz]);
      sizes[i] = combis[i].length;

    }
    it = new MixedRadixEnumeration(sizes);
  }

  @Override
  public boolean hasMoreElements() {
    return it.hasMoreElements();
  }

  @Override
  public int[] nextElement() {
    int[] mr = it.nextElement();

    ArrayList<Integer> pos = new ArrayList<Integer>();
    int[] x = new int[n];

    for (int i = 0; i < n; i++) {
      x[i] = 0;
      pos.add(i);
    }

    for (int i = mr.length - 1; i >= 0; i--) {
      Combination p = combis[i][mr[i]];
      for (int j = p.getN() - 1; j >= 0; j--) {
        if (p.get(j)) {
          x[pos.get(j)] = nonzeroindices.get(i);
          pos.remove((int)j);
        }
      }
    }
    
    for (int i = 0; i < x.length / 2; i++) {
      int temp = x[i];
      x[i] = x[x.length - i - 1];
      x[x.length - i - 1] = temp;
    }
    return x;
  }
}
