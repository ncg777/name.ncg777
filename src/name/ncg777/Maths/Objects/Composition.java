package name.ncg777.Maths.Objects;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import name.ncg777.CS.Backtracker;
import name.ncg777.Statistics.SegmentationScore;


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
 * @see Backtracker
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

  public Sequence partitionByEquality() {
    Sequence s = this.asSequence();
    Sequence groups = new Sequence();
    for(int i=0;i<s.size();i++) groups.add(0);
    
    int k=0;
    for(int j=s.size()-1;j>=0;j--) {
      if(s.get(0) == s.get(j)) {k--;}
      else {break;}
    }
    k += s.size();
    k = k %s.size();
    int previousValue = s.get(k);
    int currentGroup = 0;
    
    for(int i=k+1; i<s.size() + k;i++) {
      int v = s.get(i%s.size());
      if(v != previousValue) {
        currentGroup++;
      }
      groups.set(i%groups.size(), currentGroup);
      previousValue = v;  
    }
    return groups;
  }
  public List<Composition> segment() {
    Backtracker<Composition> b =
        Backtracker.Maximizer((c) -> Composition.refinements(c),
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
    
    b.backtrack(new Composition(getK()+1), o);
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
}
