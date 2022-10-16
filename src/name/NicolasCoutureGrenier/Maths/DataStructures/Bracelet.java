package name.NicolasCoutureGrenier.Maths.DataStructures;


import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;


public class Bracelet extends Sequence {

  private static final long serialVersionUID = 1L;

  private Integer m_Order;
  static Integer cnt;

  public Integer getOrder() {
    return m_Order;
  }

  private Bracelet(Integer k, Integer p_Order, List<Integer> array) {
    super(array);
    boolean ex = false;
    for (int i = 0; i < array.size(); i++) {
      if (array.get(i) >= k || array.get(i) < 0) {
        ex = true;
        break;
      }
    }

    if (ex) {
      throw new IllegalArgumentException("Bracelet constructor received incoherent arguments.");
    }
    m_Order = p_Order;
  }


  static int[] a;

  static int checkRev(int t, int i) {
    for (int j = i + 1; j < (t + 1) / 2; j++) {
      if (a[j] < a[t - j + 1]) {
        return 0;
      }
      if (a[j] > a[t - j + 1]) {
        return -1;
      }
    }
    return 1;
  }

  public static TreeSet<Bracelet> generate(int n, int k) {
    a = new int[n + 1];
    for (int i = 0; i <= n; i++) {
      a[i] = 0;
    }

    cnt = 0;
    TreeSet<Bracelet> output = new TreeSet<Bracelet>();

    subGen(n, k, 1, 1, 0, 0, 0, false, output);
    return output;
  }

  private static void subGen(int n, int k, int t, int p, int r, int u, int v, boolean rs,
      TreeSet<Bracelet> output) {
    if (t - 1 > (n - r) / 2 + r) {
      if (a[t - 1] > a[n - t + 2 + r]) {
        rs = false;
      } else if (a[t - 1] < a[n - t + 2 + r]) {
        rs = true;
      }
    }
    if (t > n) {
      if (!rs && n % p == 0) {
        Integer[] tmp = new Integer[n];
        for (int i = 0; i < n; i++) {
          tmp[i] = a[i + 1];
        }

        output.add(new Bracelet(k, cnt, Arrays.asList(tmp)));


      }
    } else {
      a[t] = a[t - p];
      if (a[t] == a[1]) {
        v++;
      } else {
        v = 0;
      }
      if (u == t - 1 && a[t - 1] == a[1]) {
        u++;
      }
      if (t == n && u != n && a[n] == a[1]) {
        ;
      } else if (u == v) {
        int rev = checkRev(t, u);
        if (rev == 0) {
          subGen(n, k, t + 1, p, r, u, v, rs, output);
        }
        if (rev == 1) {
          subGen(n, k, t + 1, p, t, u, v, false, output);
        }
      } else {
        subGen(n, k, t + 1, p, r, u, v, rs, output);
      }
      if (u == t) {
        u--;
      }

      for (int j = a[t - p] + 1; j <= k - 1; j++) {
        a[t] = j;
        if (t == 1) {
          subGen(n, k, t + 1, t, 1, 1, 1, rs, output);
        } else {
          subGen(n, k, t + 1, t, r, u, 0, rs, output);
        }

      }

    }
  }

}
