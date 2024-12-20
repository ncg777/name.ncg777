package name.ncg777.maths.words;

import static name.ncg777.computing.structures.CollectionUtils.calcIntervalVector;

import java.io.Serializable;
import java.math.BigInteger;
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
  
  public BinaryWord(BigInteger natural, int length) {
    super(length);
    if(BigInteger.TWO.pow(length).subtract(BigInteger.ONE).bitLength() < natural.bitLength())
      throw new IllegalArgumentException("Not enough bits.");

    int i=0;
    while (length-- > 0) {
      BigInteger r = natural.mod(BigInteger.TWO);
      this.set(i++, r.equals(BigInteger.ONE));
      natural = natural.subtract(r).divide(BigInteger.TWO);
    }
  }
  
  public Word toWord(Alphabet.Name alphabetName) {
    return new Word(alphabetName, this);
  }
  
  public List<? extends Combination> decomposeIntoHomogeneousRegions() {
    return super.reverse().decomposeIntoHomogeneousRegions().stream().map((c) -> BinaryWord.build(c)).toList();
  }
  
  
  public BinaryWord scaleModulo(int k, int n){
    if(this.getN() % n != 0){
      throw new RuntimeException("n does not divide the length of the rhythm.");
    }
    
    var b = new BitSet(this.getN());
    for(int i=0; i<this.getN();i++)
    {
      b.set(i, this.get((i*k)%n + n*(i/n)));
    }
    return build(b, this.getN());
  }
  
  public static BinaryWord build(String input) {
    var sb = new StringBuilder(input);
    return build(Combination.fromBinaryString(sb.reverse().toString()));
  }
  
  public static BinaryWord build(Combination c) {
    return new BinaryWord(c.reverse(), c.getN());
  }
   
  public static BinaryWord build(BitSet p_bs, int size) {
    return new BinaryWord(p_bs, size);
  }
  
  public static BinaryWord build(TreeSet<Integer> p_arr, int p_length) {
    return new BinaryWord(p_arr, p_length);
  }

  public static BinaryWord rotate(BinaryWord r, int t) {
    return new BinaryWord(r.rotate(t), r.m_n);
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
  
  @Override
  public BinaryWord reverse() {
    return new BinaryWord(super.reverse(), this.getN());
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
  

  
  @Override
  public String toString() {
    var sb = new StringBuilder(this.toBinaryString());
    return sb.reverse().toString();
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
  
  
}
