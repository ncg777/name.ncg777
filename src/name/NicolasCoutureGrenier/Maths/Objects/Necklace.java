package name.NicolasCoutureGrenier.Maths.Objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;



public class Necklace extends Sequence {

  private static final long serialVersionUID = 1L;
  private Integer m_Order;
  static Integer cnt;

  public Integer getOrder() {
    return m_Order;
  }

  private Necklace(Integer k, Integer p_Order, List<Integer> array) {
    super(array);
    boolean ex = false;
    for (int i = 0; i < array.size(); i++) {
      if (array.get(i) >= k || array.get(i) < 0) {
        ex = true;
        break;
      }
    }

    if (ex) {
      throw new IllegalArgumentException("Necklace constructor received incoherent arguments.");
    }
    m_Order = p_Order;

  }

  public static TreeSet<Necklace> generate(int n, int k) {
    cnt = 0;
    TreeSet<Necklace> output = new TreeSet<Necklace>();
    ArrayList<Integer> a = new ArrayList<Integer>();
    for (int i = 0; i < n + 1; i++) {
      a.add(0);
    }
    subGen(1, 1, n, k, a, output);
    cnt = 0;
    return output;
  }

  private static void subGen(int t, int p, int n, int k, ArrayList<Integer> a,
      TreeSet<Necklace> output) {
    if (t > n) {
      if ((n % p) == 0) {
        Integer[] tmp = new Integer[n];
        for (int i = 0; i < n; i++) {
          tmp[i] = a.get(i + 1);
        }

        output.add(new Necklace(k, cnt, Arrays.asList(tmp)));
        cnt++;

      }
    } else {
      a.set(t, a.get(t - p));
      subGen(t + 1, p, n, k, a, output);
      for (int j = a.get(t - p) + 1; j < k; j++) {
        a.set(t, j);
        subGen(t + 1, t, n, k, a, output);
      }
    }
  }



}
