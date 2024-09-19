package name.NicolasCoutureGrenier.Maths.Enumerations;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * Enumerates all values of the mixed radix base given as parameter.
 * 
 * @link http://en.wikipedia.org/wiki/Mixed_radix
 * 
 */
public class MixedRadixEnumeration implements Enumeration<Integer[]> {
  private Integer[] base;
  private Integer[] current;
  private boolean isLast = false;

  public MixedRadixEnumeration(List<Integer> base0){
    Integer[] base = new Integer[base0.size()];
    for(Integer i=0;i<base0.size();i++){
      base[i] = base0.get(i);
    }
    init(base);
  }
  
  private void init(Integer[] base){
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
  public MixedRadixEnumeration(Integer[] base) {
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
  public Integer[] nextElement() {
    if (!hasMoreElements()) {
      throw new IllegalStateException(
          "Utilities.MixedRadixEnumeration - call on nextElement, but there's no next element!");
    }

    Integer[] o = new Integer[base.length];
    if (current == null) {
      for (Integer i = 0; i < base.length; i++) {
        o[i] = 0;
      }
    } else {
      o = Arrays.copyOf(current, current.length);
      for (Integer i = 0; i < base.length; i++) {
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

  public static Integer[] mapIndexToCoordinates(Integer index, Integer[] base) {
    Integer[] o = new Integer[base.length];
    Integer t = index;

    for (Integer k = base.length - 1; k >= 0; k--) {
      Integer b = base[k];
      o[k] = t % b;
      t = t / b;
    }
    return o;
  }
  
}
