package name.NicolasCoutureGrenier.Maths.Enumerations;

import java.util.Enumeration;

import name.NicolasCoutureGrenier.Maths.Objects.Combination;

public class CombinationEnumeration implements Enumeration<Combination> {
  private Combination current;

  public CombinationEnumeration(int n, int k) {
    super();
    current = first(n, k);
  }

  private static Combination first(int n, int k) {
    if(k>n || n<0 || k<0){return null;}
    Combination o = new Combination(n);
    
    for (int i = 0; i < k; i++) {
      o.set(i, true);
    }
    return o;
  }


  private static Combination next(Combination c) {
    int n = c.getN();

    Combination o = null;

    int j = -1;
    for (int i = 0; i < n - 1; i++) {
      if (c.get(i) && !c.get(i + 1)) {
        j = i;
        break;
      }
    }
    if (j != -1) {
      o = new Combination(c);
      o.set(j, false);
      o.set(j + 1, true);
      int s = -1;
      for (int i = 0; i < j; i++) {
        if (!o.get(i)) {
          s = i;
          break;
        }
      }

      for (int i = j; i >= 0; i--) {
        if (o.get(i) && s != -1 && s < i) {
          o.set(i, false);
          o.set(s, true);
          while (s < j && o.get(s)) {
            s++;
          }

        }
      }
    }
    return o;

  }

  @Override
  public boolean hasMoreElements() {
    return current != null;
  }

  @Override
  public Combination nextElement() {
    Combination o = current;
    current = next(current);
    return o;
  }
}
