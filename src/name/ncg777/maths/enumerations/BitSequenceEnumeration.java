package name.ncg777.maths.enumerations;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.TreeSet;

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
    
    FiniteHomoRelation<ImmutableIntArray> relation = new FiniteHomoRelation<>();
    
    // Generate all points and their neighbors efficiently: O(n * 2^n)
    BitSequenceEnumeration enumeration = new BitSequenceEnumeration(dimension);
    while (enumeration.hasMoreElements()) {
      Sequence seq = enumeration.nextElement();
      ImmutableIntArray point = sequenceToImmutableIntArray(seq);
      
      // For each point, generate neighbors by flipping each bit
      for (int i = 0; i < dimension; i++) {
        int[] neighborArray = new int[dimension];
        for (int j = 0; j < dimension; j++) {
          neighborArray[j] = seq.get(j);
        }
        // Flip the i-th bit
        neighborArray[i] = 1 - neighborArray[i];
        ImmutableIntArray neighbor = new ImmutableIntArray(neighborArray);
        
        // Add the relation pair
        relation.add(point, neighbor);
      }
    }
    
    return relation;
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