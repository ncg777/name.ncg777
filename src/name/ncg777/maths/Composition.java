package name.ncg777.maths;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import name.ncg777.computing.RecursiveOptimizer;
import name.ncg777.maths.enumerations.MixedRadixEnumeration;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.statistics.SegmentationScore;

/**
 * The {@code Composition} class represents a specific type of combinatorial structure that
 * extends the functionalities of the {@code Combination} class. It encapsulates a bitset that
 * represents a composition of elements, providing methods to manipulate and analyze these
 * compositions in various ways.
 *
 * <p>This class is particularly useful in mathematical contexts where compositions and their
 * properties are sought, including in applications such as music theory and combinatorial
 * optimization.</p>
 *
 * <p>Key functionalities include:</p>
 * <ul>
 * <li>Constructors for creating compositions based on boolean lists, bitsets, or integer sizes.</li>
 * <li>Conversion methods to transform a composition into a sequence of integers, indicating 
 * the lengths of segments in the composition.</li>
 * <li>Degradation of compositions to modify their structure while retaining certain properties.</li>
 * <li>Methods for partitioning by equality, providing groupings of similar values within the 
 * composition.</li>
 * <li>Segmentation capabilities through backtracking, allowing for optimized refinement into 
 * meaningful structures.</li>
 * </ul>
 *
 * <p>Instances of {@code Composition} can represent musical segments, statistical distributions, 
 * or other applications of combinatorial mathematics where sequences of values matter.</p>
 *
 * @see Combination
 * @see SegmentationScore
 * @see RecursiveOptimizer
 */
public class Composition extends Combination {
  private static final long serialVersionUID = 1L;

  public Composition(List<Boolean> comp) {
    super(comp.size());
    for (int i = 0; i < m_n; i++) {
      if (comp.get(i).booleanValue()) {
        this.set(i, comp.get(i));
      }
    }
  }

  /**
   * There is an implicit 1 at the beginning of the array, i.e. the total will be the size of the
   * array + 1
   * 
   * @param comp
   */
  public Composition(Boolean[] comp) {
    super(comp.length);
    for (int i = 0; i < m_n; i++) {
      if (comp[i].booleanValue()) {
        this.set(i, comp[i]);
      }
    }
  }

  public Composition(int n) {
    super(n - 1);
  }

  public Composition() {
    super(0);
  }

  public Composition(BitSet x, int n) {
    super(x, n - 1);
  }

  public Composition(Combination c) {
    super(c);
  }

  public Integer getTotal() {
    return m_n + 1;
  }

  public Sequence asSequence() {
    Sequence o = new Sequence();
    int n = 1;
    for (int i = 0; i < m_n; i++) {
      if (this.get(i)) {
        o.add(n);
        n = 1;
      } else {
        n++;
      }
    }
    o.add(n);
    return o;

  }

  /**
   * Enumerates all ordered factor refinements of each segment in this composition and returns the
   * resulting compositions as a set.
   */
  public Set<Composition> factorRefinements() {
    Sequence parts = asSequence();
    List<List<int[]>> factorPairs = new ArrayList<>();
    List<Integer> bases = new ArrayList<>();

    for (int part : parts) {
      if (part <= 0) {
        throw new IllegalArgumentException("Composition contains non-positive segment length.");
      }
      List<int[]> pairs = new ArrayList<>();
      for (int a = 1; a <= part; a++) {
        if (part % a == 0) {
          pairs.add(new int[] {a, part / a});
        }
      }
      factorPairs.add(pairs);
      bases.add(pairs.size());
    }

    Set<Composition> out = new TreeSet<>();
    MixedRadixEnumeration mre = new MixedRadixEnumeration(bases);
    while (mre.hasMoreElements()) {
      int[] indices = mre.nextElement();
      Sequence refined = new Sequence();
      for (int i = 0; i < parts.size(); i++) {
        int[] pair = factorPairs.get(i).get(indices[i]);
        int count = pair[0];
        int size = pair[1];
        for (int j = 0; j < count; j++) {
          refined.add(size);
        }
      }
      out.add(fromSequence(refined));
    }
    return out;
  }
  public Combination asCombination() {
    Combination o = new Combination(m_n + 1);
    o.set(0);
    for(int i=1;i<m_n+1; i++) {
      o.set(i, this.get(i-1));
    }
    return o;
  }
  
  public Composition degrade() {
    int c = this.cardinality();
    if (c == 0) {
      return new Composition();
    }

    Composition o = new Composition(getTotal() - 1);
    if (c == 1) {
      return o;
    }

    int i = this.nextSetBit(0); // it can't be that i = -1

    for (int j = 0; j < o.m_n; j++) {
      o.set(j, this.get((j + i + 1) % o.m_n));
    }
    return o;
  }

  
  
  
  
  public List<Composition> segment() {
    RecursiveOptimizer<Composition> b =
        RecursiveOptimizer.Maximizer((c) -> Composition.refinements(c),
            (c) -> {
        Sequence s = Composition.this.asSequence();
        
        double[] d = new double[s.size()];
        int k=0;
        for(Integer i: s){
          d[k++] = i.doubleValue();
        }
        
        return SegmentationScore.evaluate(d, c);
      });
    List<Composition> o = new ArrayList<Composition>();
    
    b.optimize(new Composition(getK()+1), o);
    return o;
    
  }
  
  public <T> List<List<T>> segmentList(List<T> s) {
    if(this.getTotal() != s.size()) throw new IllegalArgumentException();
    var o = new ArrayList<List<T>>();
    var cs = this.asSequence();
    int start = 0;
    for(int i=0;i<cs.size();i++) {
      o.add(s.subList(start, start+cs.get(i)));
      start += cs.get(i);
    }
    return o;
  }
  
  public List<String> segmentString(String str) {
    if(this.getTotal() != str.length()) throw new IllegalArgumentException();
    var o = new ArrayList<String>();
    var s = this.asSequence();
    int start = 0;
    for(int i=0;i<s.size();i++) {
      o.add(str.substring(start, start+s.get(i)));
      start += s.get(i);
    }
    return o;
  }
  @Override
  public String toString() {
    return asSequence().toString();
  }

  /**
   * @see Combination#refinements()
   */
  public static List<Composition> refinements(Composition co) {
    List<Combination> c = Combination.refinements(co);
    if (c == null) {
      return null;
    }
    List<Composition> o = new ArrayList<Composition>(c.size());
    for (int i = 0; i < c.size(); i++) {
      o.add(new Composition(c.get(i)));
    }
    return o;
  }

  private static Composition fromSequence(Sequence seq) {
    if (seq == null || seq.isEmpty()) {
      throw new IllegalArgumentException("Sequence must be non-empty.");
    }
    int total = seq.sum();
    if (total < 1) {
      throw new IllegalArgumentException("Sequence must have a positive sum.");
    }

    List<Boolean> marks = new ArrayList<>(Math.max(0, total - 1));
    for (int i = 0; i < total - 1; i++) {
      marks.add(false);
    }

    int acc = 0;
    for (int i = 0; i < seq.size(); i++) {
      int part = seq.get(i);
      if (part <= 0) {
        throw new IllegalArgumentException("Sequence contains non-positive segment length.");
      }
      acc += part;
      if (i < seq.size() - 1) {
        marks.set(acc - 1, true);
      }
    }
    if (acc != total) {
      throw new IllegalArgumentException("Sequence sum mismatch.");
    }
    return new Composition(marks);
  }
}
