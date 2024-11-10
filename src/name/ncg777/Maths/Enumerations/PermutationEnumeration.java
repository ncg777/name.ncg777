package name.ncg777.Maths.Enumerations;

import java.util.Arrays;
import java.util.Enumeration;

public class PermutationEnumeration implements Enumeration<Integer[]> {
  private Integer[] p;

  // Constructor. WARNING: Don't make n too large.
  public PermutationEnumeration(int n) {
    super();
    if (n < 0) {
      throw new Error("Min 0");
    }
    this.p = new Integer[n];
    for (int i = 0; i < n; i++)
      this.p[i] = i;
  }


  // Generate next permutation
  public static Integer[] getNext(Integer[] a0) {
    var a = Arrays.copyOf(a0, a0.length);

    // Find largest index j with a[j] < a[j+1]
    int j = a.length - 2;
    while (j >= 0 && a[j] > a[j + 1]) {
      j--;
    }

    if (j < 0) {
      return null; // No more permutations
    }

    // Find index k such that a[k] is smallest integer greater than a[j]
    int k = a.length - 1;
    while (a[j] > a[k]) {
      k--;
    }

    // Interchange a[j] and a[k]
    {
      int tmp = a[k];
      a[k] = a[j];
      a[j] = tmp;
    }

    // Put tail end of permutation after jth position in increasing order
    int r = a.length - 1;
    int s = j + 1;

    while (r > s) {
      {
        int tmp = a[s];
        a[s] = a[r];
        a[r] = tmp;
      }
      r--;
      s++;
    }
    return a;
  }

  public boolean hasMoreElements() {
    return this.p != null;
  }

  public Integer[] nextElement() {
    var o = this.p;
    if (o == null) {
      throw new RuntimeException("No such element.");
    }

    this.p = PermutationEnumeration.getNext(this.p);

    return o;
  }
}
