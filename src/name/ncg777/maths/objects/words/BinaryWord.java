package name.ncg777.maths.objects.words;

import static name.ncg777.computerScience.dataStructures.CollectionUtils.calcIntervalVector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import name.ncg777.computerScience.dataStructures.CollectionUtils;
import name.ncg777.maths.objects.Combination;
import name.ncg777.maths.objects.Composition;
import name.ncg777.maths.objects.Sequence;

public class BinaryWord extends Combination implements Serializable {
  
  private static final long serialVersionUID = 1L;
  protected BinaryWord(int n, Set<Integer> s) {
    super(n,s);
  }
  public BinaryWord(BitSet b, int n) {
    super(b, n);
  }
  public static BinaryWord parse(String input) {    
    String binstr = Integer.toBinaryString(Integer.parseInt(input, 2));
    
    BitSet b = new BitSet(binstr.length()*2);
    
    for(int i=0;i<binstr.length();i++) {
      b.set(i, binstr.charAt(i) == '1');
    }
    return new BinaryWord(b, binstr.length());
  }
  public ArrayList<BinaryWord> partitionByEquality() {
    ArrayList<BinaryWord> o = new ArrayList<BinaryWord>();
    Sequence seq = this.asSequence();
    Sequence partition = this.getComposition().partitionByEquality();
    
    int n = partition.getMax()+1;
    
    for(int i=0;i<n;i++) {
      o.add(new BinaryWord(new BitSet(this.getN()), this.getN()));
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
  
  public BinaryWord scaleModulo(int k, int n){
    return scaleModulo(this, k, n);
  }
  
  public BinaryWord juxtapose(BinaryWord other) {
    int ns = this.getN()+other.getN();
    BitSet bs = new BitSet(ns);
    for(int i=0;i<this.getN();i++){bs.set(i,this.get(i));}
    for(int i=0;i<other.getN();i++){bs.set(i+this.getN(),other.get(i));}
    BinaryWord r = BinaryWord.buildRhythm(bs, ns);
    return r;
  }
  
  public static BinaryWord buildRhythm(String p_str) {
    int l = p_str.length();
    TreeSet<Integer> t = new TreeSet<Integer>();

    for (int i = 0; i < l; i++) {
      if (!p_str.substring(i, i + 1).equals("0")) {
        t.add(i);
      }
    }

    return new BinaryWord(l, t);
  }

  public static BinaryWord buildRhythm(Combination c) {
    return new BinaryWord(c, c.getN());
  }
  
  public static BinaryWord buildRhythm(BitSet p_bs, int size) {
    int l = size;
    TreeSet<Integer> t = new TreeSet<Integer>();

    for (int i = 0; i < l; i++) {
      if (p_bs.get(i)) {
        t.add(i);
      }
    }

    return new BinaryWord(l, t);
  }
  
  public static BinaryWord buildRhythm(Boolean[] p_arr) {
    int l = p_arr.length;
    TreeSet<Integer> t = new TreeSet<Integer>();

    for (int i = 0; i < l; i++) {
      if (p_arr[i]) {
        t.add(i);
      }
    }

    return new BinaryWord(l, t);
  }

  public static BinaryWord buildRhythm(int p_length, TreeSet<Integer> p_arr) {
    return new BinaryWord(p_length, p_arr);
  }

  public static BinaryWord rotate(BinaryWord r, int t) {
    return new BinaryWord(r.rotate(t), r.m_n);

  }

  public static BinaryWord scaleModulo(BinaryWord r, int k, int n) {
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

    return getComposition().asSequence().cyclicalDifference().signs();
  }
  
  public Sequence getShadowContour() {
    if(getK() == 0) return new Sequence();
    Sequence a = getComposition().asSequence();

    Sequence mid = new Sequence();
    for(int i=1;i<=a.size();i++) {
      mid.add(a.get(i-1) + a.get(i%a.size()));
    }
    
    return mid.cyclicalDifference().signs();
  }
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final BinaryWord other = (BinaryWord) obj;

    return (super.equals(other)) && (this.m_n == other.m_n);
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  protected BinaryWord(Integer p_n, Set<Integer> p_s) {
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

  public static boolean equivalentUnderRotation(BinaryWord a, BinaryWord b) {
    for (int i = 0; i < a.m_n; i++) {
      if (a.equals(rotate(b, i))) {
        return true;
      }
    }
    return false;
  }
  private static TreeMap<BinaryWord, Sequence> specMemo = new TreeMap<>();
  public static Sequence calcSpectrum(BinaryWord r) {
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
      return q.copy();
    } else {
      specMemo.put(r, s.get(1));
      return s.get(1).copy();
    }

  }
  
  public static BinaryWord merge(List<BinaryWord> r) {
    Sequence sizes = new Sequence();
    for(int i=0;i<r.size();i++) {sizes.add(r.get(i).getN());}
    int max=sizes.getMax();
    int newsz = max*r.size();
    BitSet b =new BitSet(newsz);
    
    for(int i=0;i<newsz;i++) {b.set(i,r.get(i%r.size()).get((i/r.size())%r.get(i%r.size()).getN()));}
    return new BinaryWord(b, newsz);
  }

}
