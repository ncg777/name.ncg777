package name.ncg777.maths.words;

import static name.ncg777.computing.structures.CollectionUtils.calcIntervalVector;
import static com.google.common.math.IntMath.checkedPow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import name.ncg777.computing.structures.CollectionUtils;
import name.ncg777.maths.Combination;
import name.ncg777.maths.Composition;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.words.Alphabet.Name;

public class BinaryWord extends Combination implements Serializable {
  private static final long serialVersionUID = 1L;
  
  public BinaryWord(BinaryWord binaryWord) {
    super(binaryWord);
  }
  
  public BinaryWord(Set<Integer> s, int n) {
    super(n,s);
  }
  
  public BinaryWord(BitSet b, int n) {
    super(b, n);
  }
  
  public BinaryWord(Boolean[] b) {
    super(b.length);
    for(int i=0;i<b.length;i++) this.set(i,b[i]);
  }
  
  public BinaryWord(long natural, int length) {
    super(length);
    if(checkedPow(2, length) < natural)
      throw new IllegalArgumentException("Not enough bits.");
    int n = 2;
    int i=0;
    while (length-- > 0) {
      long r = natural % n;
      this.set(i++, r==1);
      natural = (natural - r) / n;
    }
  }
  
  public Word toWord(Alphabet.Name alphabetName) {
    return new Word(alphabetName, this);
  }
  
  public List<BinaryWord> partitionByEquality() {
    ArrayList<BinaryWord> o = new ArrayList<BinaryWord>();
    Sequence seq = this.asSequence();
    Sequence partition = this.reverse().getComposition().partitionByEquality().reverse();
    
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
  
  public BinaryWord scaleModulo(int k, int n){
    return scaleModulo(this, k, n);
  }
  
  public BinaryWord juxtapose(BinaryWord other) {
    int ns = this.getN()+other.getN();
    BitSet bs = new BitSet(ns);
    for(int i=0;i<this.getN();i++){bs.set(i,this.get(i));}
    for(int i=0;i<other.getN();i++){bs.set(i+this.getN(),other.get(i));}
    BinaryWord r = BinaryWord.build(bs, ns);
    return r;
  }
  
  public static BinaryWord build(String input) {
    var sb = new StringBuilder(input);
    return build(Combination.fromBinaryString(sb.reverse().toString()));
  }
  
  public static BinaryWord build(Combination c) {
    return new BinaryWord(c, c.getN());
  }
   
  public static BinaryWord build(BitSet p_bs, int size) {
    int l = size;
    TreeSet<Integer> t = new TreeSet<Integer>();

    for (int i = 0; i < l; i++) {
      if (p_bs.get(i)) {
        t.add(i);
      }
    }

    return new BinaryWord(t, l);
  }
  
  public static BinaryWord build(Boolean[] p_arr) {
    int l = p_arr.length;
    TreeSet<Integer> t = new TreeSet<Integer>();

    for (int i = 0; i < l; i++) {
      if (p_arr[i]) {
        t.add(i);
      }
    }

    return new BinaryWord(t, l);
  }

  public static BinaryWord build(TreeSet<Integer> p_arr, int p_length) {
    return new BinaryWord(p_arr, p_length);
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
    return build(output);
  }
  
  public Sequence getContour() {
    return toWord(Name.Binary).getContour();
  }
  
  public Sequence getShadowContour() {
    return toWord(Name.Binary).getShadowContour();
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

  protected BinaryWord(Set<Integer> p_s, Integer p_n) {
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
  
  public BinaryWord invert() {
    var o = new BinaryWord(this);
    for(int i=0;i<o.size();i++) o.set(i,!o.get(i));;
    return o;
  }
  
  public BinaryWord reverse() {
    var o = new BinaryWord(new BitSet(), this.getN());
    for(int i=0;i<o.getN();i++) {
      if(this.get(i)) o.set(-1+o.getN()-i);
    }
    return o;
  }
  
  @Override
  public String toString() {
    var sb = new StringBuilder(this.toBinaryString());
    return sb.reverse().toString();
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
    BinaryWord b = new BinaryWord(new BitSet(), newsz);
    var idxs = new Sequence();
    for(int i=0;i<r.size();i++) idxs.add(0);
    
    for(int i=0;i<newsz;i++) {
      int j = -1+newsz-i;
      int wi = j%r.size();
      var w = r.get(wi);
      var idx = idxs.get(wi);
      idxs.set(wi, (idx+1)%w.size());
      
      b.set(-1+newsz-j, w.get(idx));
    }
    return b;
  }
}
