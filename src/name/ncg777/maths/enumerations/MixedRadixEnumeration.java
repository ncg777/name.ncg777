package name.ncg777.maths.enumerations;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Consumer;

import name.ncg777.computing.structures.ImmutableDoubleArray;
import name.ncg777.computing.structures.ImmutableIntArray;
import name.ncg777.maths.relations.FiniteHomoRelation;
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
  
  /**
   * Creates a TreeSet<ImmutableIntArray> using a lexicographic comparator and enumerates all points
   * based on the given base.
   *
   * @param base the mixed radix base used for enumeration
   * @return a TreeSet<ImmutableIntArray> containing all enumerated points
   */
  public static TreeSet<ImmutableIntArray> getPointSet(int[] base) {

      // Create a TreeSet with the comparator
      TreeSet<ImmutableIntArray> treeSet = new TreeSet<>();

      // Create the MixedRadixEnumeration instance
      MixedRadixEnumeration enumeration = new MixedRadixEnumeration(base);

      // Add each enumerated point to the TreeSet
      while (enumeration.hasMoreElements()) {
          treeSet.add(new ImmutableIntArray(enumeration.nextElement()));
      }

      return treeSet;
  }
  
  /**
   * Generates a TreeSet<ImmutableDoubleArray> for a parametric space defined by lbound, ubound, and subdiv.
   *
   * @param lbound The lower bounds for each dimension.
   * @param ubound The upper bounds for each dimension.
   * @param subdiv The number of subdivisions for each dimension.
   * @return A TreeSet<ImmutableDoubleArray> containing the generated points in lexicographic order.
   */
  public static TreeSet<ImmutableDoubleArray> getPointSet(double[] lbound, double[] ubound, int[] subdiv) {
      int dim = lbound.length;
      
      double[] increments = getIncrements(lbound, ubound, subdiv);
      
      TreeSet<ImmutableDoubleArray> treeSet = new TreeSet<>();

      int[] subdivPlusOne = new int[dim];
      for (int i = 0; i < dim; i++) {
          subdivPlusOne[i] = subdiv[i] + 1;
      }

      var mre = new MixedRadixEnumeration(subdivPlusOne);
      while (mre.hasMoreElements()) {
          var indices = mre.nextElement();
          double[] point = new double[dim];
          for (int i = 0; i < dim; i++) {
              point[i] = lbound[i] + (indices[i] * increments[i]);
          }
          treeSet.add(new ImmutableDoubleArray(point));
      }

      return treeSet;
  }
  
  public static void consumePointSet(
      double[] lbound,
      double[] ubound,
      int[] subdiv,
      Consumer<ImmutableDoubleArray> consumer) {
    var ps = getPointSet(lbound, ubound, subdiv);
    for(var p : ps) {
      consumer.accept(p);
    }
  }
  
  private static double[] getIncrements(double[] lbound, double[] ubound, int[] subdiv) {
    if (lbound.length != ubound.length || lbound.length != subdiv.length) {
        throw new IllegalArgumentException("Dimensions of lbound, ubound, and subdiv must match.");
    }
    int dim = lbound.length;
    double[] increments = new double[dim];
    for (int i = 0; i < dim; i++) {
        increments[i] = (ubound[i] - lbound[i]) / subdiv[i];
    }
    return increments;
  }
  
  public static FiniteHomoRelation<ImmutableDoubleArray> getNeighborRelation(double[] lbound, double[] ubound, int[] subdiv) {
    int dim = lbound.length;

    double[] increments = getIncrements(lbound,ubound,subdiv);

    int[] subdivPlusOne = new int[dim];
    for (int i = 0; i < dim; i++) {
        subdivPlusOne[i] = subdiv[i] + 1;
    }

    var nr = getNeighborRelation(subdivPlusOne);
    
    var o = new FiniteHomoRelation<ImmutableDoubleArray>();
    for(var p : nr) {
      var f = new double[dim];
      var s = new double[dim];
      
      for(int i=0;i<dim;i++) {
        f[i] = lbound[i]+p.getFirst().get(i)*increments[i];
        s[i] = lbound[i]+p.getSecond().get(i)*increments[i];
      }
      o.add(new ImmutableDoubleArray(f), new ImmutableDoubleArray(s));
    }

    return o;
  }
  
  public static FiniteHomoRelation<ImmutableIntArray> getNeighborRelation(int[] base) {
    var pointSet = getPointSet(base);

    FiniteHomoRelation<ImmutableIntArray> relation = new FiniteHomoRelation<>();

    for (var point : pointSet) {
       for(int i=0;i<base.length;i++) {
         if((point.get(i)+1) < base[i]) {
           int[] s = new int[base.length];
           for(int j=0;j<base.length;j++) {
             if(j==i) s[j] = point.get(j)+1;
             else s[j] = point.get(j);
           }
           relation.add(point, new ImmutableIntArray(s));
         }
       }
    }

    return relation;
  }

}