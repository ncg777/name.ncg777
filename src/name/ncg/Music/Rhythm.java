package name.ncg.Music;

import static name.ncg.Maths.DataStructures.CollectionUtils.calcIntervalVector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import name.ncg.Maths.DataStructures.CollectionUtils;
import name.ncg.Maths.DataStructures.Combination;
import name.ncg.Maths.DataStructures.Composition;
import name.ncg.Maths.DataStructures.Sequence;

public class Rhythm extends Combination implements Serializable {

  private static final long serialVersionUID = 1L;
  protected Rhythm(int n, Set<Integer> s) {
    super(n,s);
  }
  protected Rhythm(BitSet b, int n) {
    super(b, n);
  }
  
  public ArrayList<Rhythm> partitionByEquality() {
    ArrayList<Rhythm> o = new ArrayList<Rhythm>();
    Sequence seq = this.asSequence();
    Sequence partition = this.getComposition().partitionByEquality();
    
    int n = partition.getMax()+1;
    
    for(int i=0;i<n;i++) {
      o.add(new Rhythm(new BitSet(this.getN()), this.getN()));
      for(int j=0;j<seq.size();j++) {
        if(partition.get(j) == i) {
          o.get(i).set(seq.get(j), true);
        }
      }
    }
    return o;
  }
  
  public boolean get(int i) {
    return super.get(i%getN());
  }
  
  public Rhythm scaleModulo(int k, int n){
    return scaleModulo(this, k, n);
  }
  
  public static Rhythm buildRhythm(String p_str) {
    int l = p_str.length();
    TreeSet<Integer> t = new TreeSet<Integer>();

    for (int i = 0; i < l; i++) {
      if (!p_str.substring(i, i + 1).equals("0")) {
        t.add(i);
      }
    }

    return new Rhythm(l, t);
  }

  public static Rhythm buildRhythm(Combination c) {
    return new Rhythm(c, c.getN());
  }
  
  public static Rhythm buildRhythm(BitSet p_bs, int size) {
    int l = size;
    TreeSet<Integer> t = new TreeSet<Integer>();

    for (int i = 0; i < l; i++) {
      if (p_bs.get(i)) {
        t.add(i);
      }
    }

    return new Rhythm(l, t);
  }
  
  public static Rhythm buildRhythm(Boolean[] p_arr) {
    int l = p_arr.length;
    TreeSet<Integer> t = new TreeSet<Integer>();

    for (int i = 0; i < l; i++) {
      if (p_arr[i]) {
        t.add(i);
      }
    }

    return new Rhythm(l, t);
  }

  public static Rhythm buildRhythm(int p_length, TreeSet<Integer> p_arr) {
    return new Rhythm(p_length, p_arr);
  }

  public static Rhythm rotate(Rhythm r, int t) {
    return new Rhythm(r.rotate(t), r.m_n);

  }

  public static Rhythm scaleModulo(Rhythm r, int k, int n) {
    if(r.getN() % n != 0){
      throw new RuntimeException("n does not divide the length of the rhythm.");
    }
    
    Boolean[] output = new Boolean[r.getN()];
    for(int i=0; i<r.getN();i++)
    {
      output[i] = r.get((i*k)%n + n*(i/n));
    }
    return buildRhythm(output);
  }
  
  public Sequence getContour() {
    if(getK() == 0) return new Sequence();

    return getComposition().asSequence().cyclicalForwardDifference().signs();
  }
  
  public Sequence getShadowContour() {
    if(getK() == 0) return new Sequence();
    Sequence a = getComposition().asSequence();

    Sequence mid = new Sequence();
    for(int i=1;i<=a.size();i++) {
      mid.add(a.get(i-1) + a.get(i%a.size()));
    }
    
    return mid.cyclicalForwardDifference().signs();
  }
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Rhythm other = (Rhythm) obj;

    return (super.equals(other)) && (this.m_n == other.m_n);
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  protected Rhythm(Integer p_n, Set<Integer> p_s) {
    super(p_n, p_s);
  }

  public double compositionEntropy() {
    return this.getComposition().asSequence().entropy();
  }
  public Sequence randomSequence(){
    Composition c = getComposition();
    Sequence cs = c.asSequence();
    Sequence s = CollectionUtils.chooseAtRandom(c.segment()).asSequence();
    System.out.println(cs.toString());
    System.out.println(s.toString());
    int nbBlocks = s.size();
    Sequence[] blocks = new Sequence[nbBlocks];
    int[] ranges = new int[nbBlocks];
    int[] mins = new int[nbBlocks];
    int k=0;
    for(int i=0;i<nbBlocks;i++) {
      blocks[i] = new Sequence();
      for(int j=0;j<s.get(i);j++) {
        blocks[i].add(cs.get(k++)); 
      }
      System.out.println(blocks[i].toString());
      mins[i] = blocks[i].getMin();
      ranges[i] = 1+blocks[i].getMax()-blocks[i].getMin();
      System.out.println(mins[i]);
      System.out.println(ranges[i]);
    }
    Sequence o = new Sequence();
    
    int b = 1;
    k = 0;
    int off = 0;
    for(int i=0;i<nbBlocks;i++) {
      for(int j=0;j<s.get(i);j++) {
        int v = cs.get(k++);
        o.add(off+v-mins[i]);
      }
      
      off +=ranges[i]*b;
      b*=-1;
    }
    return o;
  }
  
  @Override
  public String toString() {
    return this.toBinaryString();
  }

  public static boolean equivalentUnderRotation(Rhythm a, Rhythm b) {
    for (int i = 0; i < a.m_n; i++) {
      if (a.equals(rotate(b, i))) {
        return true;
      }
    }
    return false;
  }
  private static TreeMap<Rhythm, Sequence> specMemo = new TreeMap<>();
  public static Sequence calcSpectrum(Rhythm r) {
    if(specMemo.containsKey(r)) return specMemo.get(r);
    
    Integer[] x = new Integer[r.getN()];

    for (int i = 0; i < r.getN(); i++) {
      x[i] = r.get(i) ? 1 : 0;
    }
    TreeMap<Integer, Sequence> s = calcIntervalVector(x);
    if (!s.containsKey(1)) {
      Sequence q = new Sequence();
      for (int i = 0; i < r.getN()/2; i++) {
        q.add(0);
      }

      specMemo.put(r, q);
      return q;
    } else {
      specMemo.put(r, s.get(1));
      return s.get(1);
    }

  }
}
