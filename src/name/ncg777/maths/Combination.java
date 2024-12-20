package name.ncg777.maths;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Joiner;

import name.ncg777.computing.structures.CollectionUtils;
import name.ncg777.maths.enumerations.CombinationEnumeration;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.statistics.RandomNumberGenerator;

import java.io.Serializable;
import java.lang.reflect.Array;

/**
 * The {@code Combination} class represents a combination of elements using a 
 * {@code BitSet} to efficiently store and manipulate sets of binary values. 
 * This class allows for operations related to combinatorial mathematics, including 
 * set operations, distance calculations, and the generation of combinations.
 * 
 * <p>{@code Combination} extends {@code BitSet}, facilitating operations 
 * for sets of binary values while adding additional functionalities specific 
 * to combinatorial analysis.</p>
 *
 * <p>Key features include:</p>
 * <ul>
 * <li>Construction of combinations using different constructors that accept parameters 
 * such as size, set membership, or binary representations.</li>
 * <li>Access to the size of the combination with {@link #getN()} and the 
 * cardinality (number of elements) with {@link #getK()}.</li>
 * <li>Calculation of the span of the combination using {@link #calcSpan()}.</li>
 * <li>Support for calculating distances between combinations with
 * {@link #calcNormalizedDistanceWith(Combination)}.</li>
 * <li>Symmetric difference and intersection operations through methods like 
 * {@link #symmetricDifference(Combination)} and {@link #intersect(Combination)}.</li>
 * <li>Generation of subdivisions (partitions) of the combination with 
 * {@link #partition(Integer[])}.</li>
 * <li>Methods for generating all combinations of a given size with 
 * {@link #generate(int, int)}.</li>
 * <li>Random combination generation via {@link #genRnd(int)} and 
 * {@link #genRnd(int, int)}.</li>
 * </ul>
 *
 * <p>This class is designed to be used in conjunction with other classes related to 
 * collections of sets and should provide a basis for various combinatorial operations 
 * relevant in applications such as music theory, combinatorial enumeration, and 
 * computational mathematics.</p>
 *
 * @see BitSet
 * @see Sequence
 */
public class Combination extends BitSet implements Comparable<Combination>, Serializable {

  private static final long serialVersionUID = 1L;
  protected int m_n;

  public Combination(Integer p_n) {
    super(p_n);
    m_n = p_n;
  }

  public int getN() {
    return m_n;
  }

  public int getK() {
    return this.cardinality();
  }

  public int calcSpan() {
    return getN() - getComposition().asSequence().getMax();
  }
  
  public Sequence getIntervalVector() {
    return CollectionUtils.calcIntervalVector(this, m_n);
  }

  public Combination(Integer p_n, Set<Integer> p_s) {
    this(p_n);
    for (int i = 0; i < p_n; i++) {
      this.set(i, p_s.contains(i));
    }
  }

  public Combination(Combination c) {
    this(c.m_n);
    this.or(c);
  }

  public Combination(BitSet c, int n) {
    this(n);
    this.or(c);
  }
  
  public Combination reverse() {
    var o = new Combination(new BitSet(), this.getN());
    for(int i=0;i<o.getN();i++) {
      if(this.get(i)) o.set(-1+o.getN()-i);
    }
    return o;
  }
  
  public Composition getComposition() {
    int nsb = nextSetBit(0);
    if(nsb == -1) {
      return new Composition(m_n);
    } else { 
      Combination t = rotate(nsb);
      List<Boolean> l = new ArrayList<Boolean>();
      for(int i=1;i<m_n;i++){ 
        l.add(t.get(i));
      }
      return new Composition(l);  
    }
  }

  public double calcNormalizedDistanceWith(Combination other) {
    int maxn = Math.max(this.getN(), other.getN());
    
    int acc = 0;
    for(int i=0; i<maxn;i++) {
      if(this.get(i%this.getN()) != other.get(i%other.getN())) acc++;
    }
    
    return ((double)acc)/((double)maxn);
  }
  
  public Combination symmetricDifference(Combination y) {
    BitSet x = new BitSet(12);
    x.or(this);
    x.xor(y);
    int n = Math.max(this.getN(), y.getN());
    return new Combination(x, n);
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + m_n;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    return this.compareTo((Combination) obj) == 0;
  }
  
  public String toBinaryString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < m_n; i++) {
      if (this.get(i)) {
        sb.append("1");
      } else {
        sb.append("0");
      }
    }
    return sb.toString();
  }
  
  public static Combination fromBinaryString(String s) {
    s = s.trim();
    int n = s.length();
    var o = new Combination(n);
    for(int i=0;i<n;i++) {
      if(s.charAt(i) != '0') o.set(i);
    }
    return o;
  }
  
  public static Combination mergeAll(List<? extends Combination> r) {
    Sequence sizes = new Sequence();
    for(int i=0;i<r.size();i++) {sizes.add(r.get(i).getN());}
    int max=sizes.getMax();
    int newsz = max*r.size();
    Combination b = new Combination(new BitSet(), newsz);
    var idxs = new Sequence();
    for(int i=0;i<r.size();i++) idxs.add(0);
    
    for(int i=0;i<newsz;i++) {
      int wi = i%r.size();
      var w = r.get(wi);
      b.set(i, w.get(idxs.get(wi)));
      idxs.set(wi, (idxs.get(wi)+1)%sizes.get(wi));
    }
    return b;
  }
  
  public Sequence homogeneityRegionsSequence() {
    Sequence s = this.getComposition().asSequence();
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
  
  public List<? extends Combination> decomposeIntoHomogeneousRegions() {
    var o = new ArrayList<Combination>();
    Sequence seq = this.getComposition().asSequence();
    Sequence partition = this.homogeneityRegionsSequence();
    
    Sequence deduped = new Sequence(); 
    Integer last = seq.get(0);
    deduped.add(last);
    for(var i : seq) {
      if(!i.equals(last)) {
        deduped.add(i);
        last = i;
      }
    }
    
    int n = partition.getMax()+1;
    
    int k = this.nextSetBit(0);
    int j=0;
    
    for(int i=0;i<n;i++) {
      var comb = new Combination(this.getN());
      int val = deduped.get(i);
      while(true) {
        comb.set(k);
        k+=val;
        if(++j == seq.size() || seq.get(j) != val) {
          break;
        }
      }
      o.add(comb);
    }
    
    return o;
  }
  
  @Override
  public String toString() {
    return "{" + Joiner.on(", ").join(this.asSequence()) + "}";
  }
  
  public Sequence asSequence(){
    Sequence o = new Sequence();
    for (int i = nextSetBit(0); i >= 0; i = nextSetBit(i + 1)) {
      o.add(i);
    }
    return o;
  }
  
  public Set<Integer> asSet(){
    Set<Integer> o = new TreeSet<Integer>();
    for (int i = nextSetBit(0); i >= 0; i = nextSetBit(i + 1)) {
      o.add(i);
    }
    return o;
  }
  
  public Sequence asBinarySequence() {
    Sequence o = new Sequence();
    for(int i=0;i<getN();i++) {
      o.add(this.get(i) ? 1 : 0);
    }
    return o;
  }
  
  public static Combination fromBinarySequence(Sequence s) {
    Combination c = new Combination(s.size());
    for(int i=0;i<s.size();i++) {
      if(s.get(i) != 0) c.set(i, true);
    }
    return c;
  }
  
  @Override
  public int compareTo(Combination o) {
    if (this.m_n < o.m_n) {
      return -1;
    }
    if (this.m_n > o.m_n) {
      return 1;
    }

    
    BitSet a = new BitSet();
    a.or(this);
    BitSet b = new BitSet();
    b.or(o);
    a.xor(b);

    int i = a.nextSetBit(0);

    if (i == -1) {
      return 0;
    } else {
      return b.get(i) ? -1 : 1;
    }
  }

  /**
   * Lists all combinations that have 1 more element than this one.
   * 
   * @return
   */
  public static List<Combination> refinements(Combination c) {
    int n = c.getN() - c.getK();
    if (n == 0) {
      return null;
    }
    List<Combination> o = new ArrayList<Combination>();
    int k = 0;
    for (int i = 0; i < n; i++) {
      while (c.get(k)) {
        k++;
      }
      BitSet b = new BitSet();
      b.or(c);
      b.set(k++);
      o.add(new Combination(b, c.getN()));
    }
    return o;
  }

  public static Combination merge(Combination a, Combination b) {
    BitSet x = new BitSet();
    x.or(a);
    x.or(b);
    return new Combination(x, Math.max(a.getN(), b.getN()));
  }
 
  public static Combination[] generate(int n, int k) {
    int cnt = (int)Numbers.binomial(n, k);
    Combination[] o = new Combination[cnt];

    CombinationEnumeration ce = new CombinationEnumeration(n, k);
    int i = 0;
    while (i < cnt) {
      o[i++] = ce.nextElement();
    }
    return o;
  }

  public Combination rotate(int t) {
    int k = -t;
    while (k < 0) {
      k += m_n;
    }
    while (k >= m_n) {
      k -= m_n;
    }

    BitSet x = new BitSet(m_n);

    for (int i = 0; i < m_n; i++) {
      x.set(i, this.get((i - k + m_n) % m_n));
    }

    return new Combination(x, m_n);
  }
  
  public Combination intersect(Combination c){
    int n = Math.min(this.getN(), c.getN());
    BitSet b = new BitSet(n);
    b.or(this); b.and(c);
    return new Combination(b, n);
  }
  
  public Combination minus(Combination c){
    Combination o = new Combination(this);
    int n = Math.min(getN(), c.getN());
    for(int i=0;i<n;i++){
      if(o.get(i) && c.get(i)){
        o.set(i,false);
      }
    }
    return o;
  }
  
  @SuppressWarnings("unchecked")
  public <T> T[] applyTo(T[] arr) {
    if(arr.length != this.getN())
      throw new IllegalArgumentException();
    
    var o = Array.newInstance(Object.class, this.getK());
    int k=0;
    for(int i=nextSetBit(0); i > -1; i=nextSetBit(i+1))
      Array.set(o, k++, arr[i]);
    return (T[])o;
  }
  
  public <T> List<T> applyTo(List<T> list) {
    if(list.size() != this.getN())
      throw new IllegalArgumentException();
    ArrayList<T> o = new ArrayList<>();
    for(int i=nextSetBit(0); i > -1; i=nextSetBit(i+1))
      o.add(list.get(i));
    return o;
  }
  
  public Sequence applyTo(Sequence seq) {
   return new Sequence(this.applyTo(seq));
  }
  
  public Combination[] partition(Sequence p0){
    Integer[] p = new Integer[p0.size()];
    int k=0;
    for(Integer i : p0) {
      p[k++] = i;
    }
    return partition(p);
  }

  public Combination[] partition(Integer[] partition){
    if(partition.length != this.getK()){
      throw new IllegalArgumentException();   
    }
    
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    for(int i=0;i<partition.length;i++){
      if(partition[i]<min){min = partition[i];}
      if(partition[i]>max){max = partition[i];}
    }
    if(min!=0 || max > getK()){
      throw new IllegalArgumentException();
    }
    int[] set = new int[getK()];
    int k=0;
    for (int i = nextSetBit(0); i > -1; i = nextSetBit(i + 1)) {
      set[k++] = i;
    }
    Combination[] o = new Combination[max+1];
    BitSet[] b = new BitSet[max+1];
    
    for(int i=0;i<=max;i++){
      b[i] = new BitSet(getN());
      for(int j=0;j<partition.length;j++){
        if(partition[j] == i){
          b[i].set(set[j]);
        }
      }
      o[i] = new Combination(b[i],getN());
    }
    
    return o;
  }

  public static Combination genRnd(int n) {
    return genRnd(n, RandomNumberGenerator.nextInt(n + 1));
  }

  public static Combination genRnd(int n, int k) {
    Combination o = new Combination(n);

    int c = 0;
    int i = 0;
    double d;

    while (c < k) {
      d = RandomNumberGenerator.nextDouble();
      if (d < (((double) (k - c)) / ((double) (n - i)))) {
        o.set(i, true);
        c++;
      }
      i++;
    }
    return o;
  }
}
