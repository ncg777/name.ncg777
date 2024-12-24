package name.ncg777.maths.numbers;

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
import name.ncg777.maths.numbers.Cipher.Name;
import name.ncg777.maths.sequences.Sequence;

public class BinaryNatural extends Combination implements Serializable {
  private static final long serialVersionUID = 1L;
  
  public BinaryNatural(BinaryNatural binaryNatural) {
    super(binaryNatural);
  }
  
  public BinaryNatural(Set<Integer> s, int n) {
    super(n,s);
  }
  
  public BinaryNatural(BitSet b, int n) {
    super(b, n);
  }
  
  public BinaryNatural(Boolean[] b) {
    super(b.length);
    for(int i=0;i<b.length;i++) this.set(i,b[i]);
  }
  
  public BinaryNatural(BigInteger natural, int length) {
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
  
  public Natural toNatural(Cipher.Name alphabetName) {
    return new Natural(alphabetName, this);
  }
  
  public List<? extends Combination> decomposeIntoHomogeneousRegions() {
    return super.reverse().decomposeIntoHomogeneousRegions().stream().map((c) -> BinaryNatural.build(c)).toList();
  }
  
  
  public BinaryNatural scaleModulo(int k, int n){
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
  
  public static BinaryNatural build(String input) {
    var sb = new StringBuilder(input);
    return build(Combination.fromBinaryString(sb.reverse().toString()));
  }
  
  public static BinaryNatural build(Combination c) {
    return new BinaryNatural(c.reverse(), c.getN());
  }
   
  public static BinaryNatural build(BitSet p_bs, int size) {
    return new BinaryNatural(p_bs, size);
  }
  
  public static BinaryNatural build(TreeSet<Integer> p_arr, int p_length) {
    return new BinaryNatural(p_arr, p_length);
  }

  public static BinaryNatural rotate(BinaryNatural r, int t) {
    return new BinaryNatural(r.rotate(t), r.m_n);
  }
  
  public Sequence getContour() {
    return toNatural(Name.Binary).getContour();
  }
  
  public Sequence getShadowContour() {
    return toNatural(Name.Binary).getShadowContour();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final BinaryNatural other = (BinaryNatural) obj;

    return (super.equals(other)) && (this.m_n == other.m_n);
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }
  
  @Override
  public BinaryNatural reverse() {
    return new BinaryNatural(super.reverse(), this.getN());
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
  
  public BinaryNatural invert() {
    var o = new BinaryNatural(this);
    for(int i=0;i<o.size();i++) o.set(i,!o.get(i));;
    return o;
  }
  

  
  @Override
  public String toString() {
    var sb = new StringBuilder(this.toBinaryString());
    return sb.reverse().toString();
  }

  private static TreeMap<BinaryNatural, Sequence> specMemo = new TreeMap<>();
  public static Sequence calcSpectrum(BinaryNatural r) {
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
