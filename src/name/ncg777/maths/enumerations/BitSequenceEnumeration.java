package name.ncg777.maths.enumerations;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiPredicate;

import name.ncg777.maths.sequences.Sequence;
import name.ncg777.computing.structures.ImmutableIntArray;
import name.ncg777.maths.relations.FiniteHomoRelation;

public class BitSequenceEnumeration implements Enumeration<Sequence> {
  private BitSetEnumeration e;
  private int n = -1;
  public BitSequenceEnumeration(int n) {
    this.n = n;
    this.e = new BitSetEnumeration(n);
  }

  @Override
  public boolean hasMoreElements() {
    return e.hasMoreElements();
  }
  
  @Override
  public Sequence nextElement() {
    BitSet b = e.nextElement();
    Sequence s = new Sequence();
    for(int i=0;i<n; i++) {
      s.add(b.get(i) ? 1 : 0);
    }
    return s;
  }
  
  /**
   * Generates all points in a hypercube of the given dimension.
   * Each point is represented as an ImmutableIntArray.
   * 
   * @param dimension the number of bits in the hypercube
   * @return a TreeSet containing all binary sequences representing points in the hypercube
   * @throws IllegalArgumentException if dimension is negative or zero
   */
  public static TreeSet<ImmutableIntArray> getPointSet(int dimension) {
    if (dimension <= 0) {
      throw new IllegalArgumentException("Dimension must be positive");
    }
    
    TreeSet<ImmutableIntArray> points = new TreeSet<>();
    
    BitSequenceEnumeration enumeration = new BitSequenceEnumeration(dimension);
    while (enumeration.hasMoreElements()) {
      Sequence seq = enumeration.nextElement();
      points.add(sequenceToImmutableIntArray(seq));
    }
    
    return points;
  }
  
  /**
   * Generates neighbor relations for points within a hypercube.
   * Each point in the hypercube can connect to its neighbors within the cube,
   * defined by flipping one bit at a time.
   * 
   * @param dimension the number of bits in the hypercube
   * @return a FiniteHomoRelation where each point is related to its neighbors
   * @throws IllegalArgumentException if dimension is negative or zero
   */
  public static FiniteHomoRelation<ImmutableIntArray> getHypercubeNeighborRelation(int dimension) {
    if (dimension <= 0) {
      throw new IllegalArgumentException("Dimension must be positive");
    }
    
    TreeSet<ImmutableIntArray> points = getPointSet(dimension);
    
    // Create a BiPredicate that determines if two points are neighbors
    BiPredicate<ImmutableIntArray, ImmutableIntArray> neighborPredicate = 
        (point1, point2) -> areNeighbors(point1, point2);
    
    return new FiniteHomoRelation<>(points, neighborPredicate);
  }
  
  /**
   * Helper method to determine if two ImmutableIntArrays are neighbors
   * (differ by exactly one bit).
   */
  private static boolean areNeighbors(ImmutableIntArray s1, ImmutableIntArray s2) {
    if (s1.size() != s2.size()) {
      return false;
    }
    
    int differences = 0;
    for (int i = 0; i < s1.size(); i++) {
      if (s1.get(i) != s2.get(i)) {
        differences++;
        if (differences > 1) {
          return false; // More than one difference, not neighbors
        }
      }
    }
    
    return differences == 1; // Exactly one difference means they are neighbors
  }
  
  /**
   * Helper method to convert a Sequence to an ImmutableIntArray.
   */
  private static ImmutableIntArray sequenceToImmutableIntArray(Sequence seq) {
    int[] arr = new int[seq.size()];
    for (int i = 0; i < seq.size(); i++) {
      arr[i] = seq.get(i);
    }
    return new ImmutableIntArray(arr);
  }
}