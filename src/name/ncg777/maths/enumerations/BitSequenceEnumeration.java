package name.ncg777.maths.enumerations;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import name.ncg777.maths.sequences.Sequence;
import name.ncg777.computing.structures.ImmutableIntArray;

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
   * @return a set containing all binary sequences representing points in the hypercube
   * @throws IllegalArgumentException if dimension is negative or zero
   */
  public static Set<ImmutableIntArray> getPointSet(int dimension) {
    if (dimension <= 0) {
      throw new IllegalArgumentException("Dimension must be positive");
    }
    
    Set<ImmutableIntArray> points = new TreeSet<>();
    
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
   * @return a map where each key is a point and values are its neighbors
   * @throws IllegalArgumentException if dimension is negative or zero
   */
  public static Map<ImmutableIntArray, List<ImmutableIntArray>> getHypercubeNeighborRelation(int dimension) {
    if (dimension <= 0) {
      throw new IllegalArgumentException("Dimension must be positive");
    }
    
    Set<ImmutableIntArray> points = getPointSet(dimension);
    Map<ImmutableIntArray, List<ImmutableIntArray>> neighborRelation = new HashMap<>();
    
    // For each point, find its neighbors
    for (ImmutableIntArray point : points) {
      List<ImmutableIntArray> neighbors = new ArrayList<>();
      
      // Check all other points to see if they are neighbors
      for (ImmutableIntArray otherPoint : points) {
        if (areNeighbors(point, otherPoint)) {
          neighbors.add(otherPoint);
        }
      }
      
      neighborRelation.put(point, neighbors);
    }
    
    return neighborRelation;
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