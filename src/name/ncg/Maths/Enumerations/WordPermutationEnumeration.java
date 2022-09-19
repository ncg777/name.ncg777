package name.ncg.Maths.Enumerations;

import java.util.ArrayList;
import java.util.Enumeration;

import name.ncg.Maths.Combination;

/**
 * Generates the combinatorial objects associated with multinomial coefficients
 * in reverse lexicographic order.
 * 
 * @author Nicolas Couture-Grenier
 */
public class WordPermutationEnumeration implements Enumeration<Integer[]> {
  private ArrayList<Integer> nonzeroindices;
  private Combination[][] combis;
  private Enumeration<Integer[]> it;
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
  public WordPermutationEnumeration(Integer[] rk) {
    super();

    if (rk == null) {
      throw new RuntimeException("MultiCombinationEnumeration, null array");
    }

    Integer k = rk.length;

    n = 0;

    nonzeroindices = new ArrayList<Integer>();

    for (Integer i = 0; i < k; i++) {
      if (rk[i] < 0) {
        throw new RuntimeException("Sequence : multiCombinationGenerate, null or negative element");
      }
      n += rk[i];
      if (rk[i] != 0) {
        nonzeroindices.add(i);
      }
    }

    Integer c = 0;

    Integer nzsz = nonzeroindices.size();
    combis = new Combination[nzsz][];

    Integer[] sizes = new Integer[nzsz];
    for (Integer i = 0; i < nzsz; i++) {
      Integer nz = nonzeroindices.get(i);
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
  public Integer[] nextElement() {
    Integer[] mr = it.nextElement();

    ArrayList<Integer> pos = new ArrayList<Integer>();
    Integer[] x = new Integer[n];

    for (Integer i = 0; i < n; i++) {
      x[i] = 0;
      pos.add(i);
    }

    for (Integer i = mr.length - 1; i >= 0; i--) {
      Combination p = combis[i][mr[i]];
      for (Integer j = p.getN() - 1; j >= 0; j--) {
        if (p.get(j)) {
          x[pos.get(j)] = nonzeroindices.get(i);
          pos.remove(j);
        }
      }

    }
    return x;
  }

}
