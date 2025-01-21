package name.ncg777.maths.enumerations;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * Enumerates all values of the mixed radix base given as parameter.
 * 
 * @link http://en.wikipedia.org/wiki/Mixed_radix
 * 
 */
public class MixedRadixEnumeration implements Enumeration<int[]> {
  private int[] base;
  private int[] current;
  private boolean isLast = false;

  public MixedRadixEnumeration(List<Integer> base0){
    int[] base = new int[base0.size()];
    for(int i=0;i<base0.size();i++){
      base[i] = base0.get(i);
    }
    init(base);
  }
  
  private void init(int[] base){
    if (base == null) {
      throw new IllegalArgumentException("Utilities.MixedRadixEnumeration - null base array");
    }
    this.base = Arrays.copyOf(base, base.length);

    for (Integer i = 0; i < base.length; i++) {
      if (base[i] < 1) {
        throw new IllegalArgumentException("Utilities.MixedRadixEnumeration - non-positive base");
      }
    }
    current = null;
    
  }
  /**
   * @param base the base
   */
  public MixedRadixEnumeration(int[] base) {
    super();
    init(base);
  }

  @Override
  public boolean hasMoreElements() {
    if (current == null) {
      return true;
    }
    if (isLast) {
      return false;
    }

    boolean o = false;
    for (Integer i = 0; i < base.length; i++) {
      if (current[i] < (base[i] - 1)) {
        o = true;
        break;
      }
    }
    if (!o) {
      isLast = true;
    }
    return o;
  }

  @Override
  public int[] nextElement() {
    if (!hasMoreElements()) {
      throw new IllegalStateException(
          "Utilities.MixedRadixEnumeration - call on nextElement, but there's no next element!");
    }

    int[] o = new int[base.length];
    if (current == null) {
      for (int i = 0; i < base.length; i++) {
        o[i] = 0;
      }
    } else {
      o = Arrays.copyOf(current, current.length);
      for (int i = 0; i < base.length; i++) {
        if (o[i] < (base[i] - 1)) {
          o[i]++;
          break;
        } else {
          o[i] = 0;
        }
      }
    }
    current = Arrays.copyOf(o, o.length);

    return o;
  }

  public static int[] mapIndexToCoordinates(long index, int[] base) {
    int[] o = new int[base.length];
    long t = index;

    for (int k = 0; k <base.length; k++) {
      long b = (long)base[k];
      o[k] = (int)(t % b);
      t = t / b;
    }
    return o;
  }
  
  public static long mapCoordinatesToIndex(int[] v, int[] base) {
    long o = 0l;
    int[] base2 = new int[base.length];
    for(int i=0;i<base.length;i++) {
      base2[i] = 1;
      for(int j=0;j<i;j++) {
        base2[i] *= base[j];
      }
    }
    
    for(int i=0;i<v.length;i++) o += (long)(v[i]*base2[i]);
    return o;
  }
}