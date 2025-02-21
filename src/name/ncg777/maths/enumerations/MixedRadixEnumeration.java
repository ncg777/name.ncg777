package name.ncg777.maths.enumerations;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import name.ncg777.maths.sequences.Sequence;
/**
 * Enumerates all values of the mixed radix base given as parameter.
 * 
 * @link http://en.wikipedia.org/wiki/Mixed_radix
 * 
 */
public class MixedRadixEnumeration implements Enumeration<int[]> {
  private int[] base;
  private int[] factor;
  private int[] current;
  private Sequence transformation = null;
  private boolean isLast = false;

  public MixedRadixEnumeration(List<Integer> base0, List<Integer> transformation, List<Integer> factor0, boolean cumulative_products){
    int[] base = new int[base0.size()];
    for(int i=0;i<base0.size();i++){
      base[i] = base0.get(i);
    }
    init(base);
    if(transformation != null) {
      if(transformation.stream().anyMatch(n -> n<0 || n>=base0.size())) {
        throw new IllegalArgumentException("transformation is out of bounds in the base");
      }
      this.transformation = new Sequence(transformation);
    } 
    
    if(factor0!=null) {
      if((transformation != null && factor0.size() != transformation.size()) || 
          (transformation == null) && factor0.size() != base0.size()) throw new IllegalArgumentException("Factor size must match base or transformation size.");
      factor = new int[factor0.size()];
      int p = 1;
      for(int i=0;i<factor0.size();i++){
        factor[i] = factor0.get(i);
        if(cumulative_products) {
          p *= factor0.get(i);
          factor[i] = p;
        }
      }
    }
  }
  public MixedRadixEnumeration(List<Integer> base0, List<Integer> transformation, List<Integer> factor0){
    this(base0,transformation,factor0,false);
  }
  public MixedRadixEnumeration(List<Integer> base0, List<Integer> transformation){
    this(base0,transformation,null,false);
  }
  
  public MixedRadixEnumeration(List<Integer> base0){
    this(base0,null,null,false);
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
    
    if(transformation != null) {
      var so = transformation.apply(new Sequence(o));
      o = new int[so.size()];
      for(int i=0;i<so.size();i++) o[i]=so.get(i);
    }
    
    if(factor != null) {
      int t = 0;
      for(int i=0;i<o.length;i++) t+=factor[i]*o[i];
      o = new int[1];
      o[0]=t;
    }
    
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
    long o = 0;
    long multiplier = 1;

    for (int i = 0; i < v.length; i++) {
        o += (long) v[i] * multiplier;
        multiplier *= base[i]; // Update multiplier for next place value
    }
    
    return o;
  }
}