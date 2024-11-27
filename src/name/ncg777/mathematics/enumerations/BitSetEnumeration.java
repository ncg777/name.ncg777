package name.ncg777.mathematics.enumerations;

import java.util.BitSet;
import java.util.Enumeration;

public class BitSetEnumeration implements Enumeration<BitSet> {
  private BitSet current;
  private int n;

  /**
   * @param base the base
   */
  public BitSetEnumeration(int n) {
    super();
    if (n < 0) {
      throw new IllegalArgumentException();
    }
    current = new BitSet(n);
    this.n = n;
  }

  @Override
  public boolean hasMoreElements() {
    return current != null;
  }

  private BitSet next(BitSet b) {
    BitSet o = new BitSet();
    o.or(b);
    boolean isLast = true;
    for (int i = 0; i < n; i++) {
      if (!o.get(i)) {
        o.set(i, true);
        isLast = false;
        break;
      } else {
        o.set(i, false);
      }
    }

    if (isLast) {
      return null;
    }

    return o;
  }

  @Override
  public BitSet nextElement() {
    BitSet o = current;
    current = next(current);
    return o;
  }

}
