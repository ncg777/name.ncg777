package name.NicolasCoutureGrenier.Maths.DataStructures;

import static com.google.common.math.IntMath.checkedPow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections4.bidimap.TreeBidiMap;

import com.google.common.base.Joiner;

import name.NicolasCoutureGrenier.Maths.Numbers;
import name.NicolasCoutureGrenier.Maths.Enumerations.PermutationEnumeration;
import name.NicolasCoutureGrenier.Music.Rhythm;
import name.NicolasCoutureGrenier.Statistics.RandomNumberGenerator;

/**
 * @author Nicolas Couture-Grenier
 */
public class Sequence extends ArrayList<Integer> implements Comparable<Sequence>, Serializable {
  private static final long serialVersionUID = 7765339983542999624L;
  public static enum ArpType
  {
    UP,
    DOWN,
    UPDOWN,
    DOWNUP,
    SIGMAUP,
    SIGMADOWN
  }
  
  public static Sequence arp(ArpType arpType, int k) {
    return arp(arpType,k,false,false);
  }
  public static Sequence arp(ArpType arpType, int k, boolean repeatBottom, boolean repeatTop) {
    if(k < 2) { throw new RuntimeException("k<2"); }
    Sequence o = new Sequence();
    switch(arpType) {
      case UP:
        o = o.juxtapose(Sequence.stair(0, k, 1));
        if(repeatBottom) {
          o.add(0,0);
        }
        if(repeatTop) {
          o.add(k-1);
        }
        break;
      case DOWN:
        o = o.juxtapose(Sequence.stair(k-1, k, -1));
        if(repeatBottom) {
          o.add(0);
        }
        if(repeatTop) {
          o.add(0, k-1);
        }
        break;
      case UPDOWN:
        o = o.juxtapose(Sequence.stair(0, k, 1));
        if(repeatBottom) {
          o.add(0,0);
        }
        if(repeatTop) {
          o.add(k-1);
        }
        o = o.juxtapose(Sequence.stair(k-2, k-2,-1));
        break;
      case DOWNUP:
        o = o.juxtapose(Sequence.stair(k-1, k, -1));
        if(repeatBottom) {
          o.add(0);
        }
        if(repeatTop) {
          o.add(0, k-1);
        }
        o = o.juxtapose(Sequence.stair(1, k-2,1));
        break;
      case SIGMAUP:
        for(int i=0; i<k;i++) {
          o = o.juxtapose(Sequence.stair(0, i+1, 1));
        }
        break;
      case SIGMADOWN:
        for(int i=0; i<k;i++) {
          o = o.juxtapose(Sequence.stair(k-1, i+1, -1));
        }
        break;
    }
    return o;
  }
  
  public static Sequence parse(String s) {
    String[] ss = s.trim().split("\\s+");
    Sequence output = new Sequence();
    for(String i : ss) {
      if(!i.trim().isEmpty()) { output.add(Integer.parseInt(i.trim()));}
    }
    return output;
  }
  
  public static Sequence stair(int o, int l, int a) {
      Sequence s = new Sequence();
      for(int i=0; i<l; i++) s.add(o+i*a);
      return s;
  }
  
  public static Sequence tri(int o, int l,  int a) { 
    return stair(o,l,a).juxtapose(stair(o+l*a,l,-a));
  }
  
  public HomogeneousFiniteBinaryRelation<Integer> toRelation() {
    var o = new HomogeneousFiniteBinaryRelation<Integer>();
    int i=0;
    for(var e : this) o.add(i++, e);
    return o;
  }
  
  public Sequence composeWith(Sequence s) {
    var rel = this.toRelation().compose(s.toRelation());
    
    var domain = rel.domain();
    if(domain.first() == 0 && domain.last() == domain.size()-1) {
      int n = domain.last();
      Sequence o = new Sequence();
      for(int i=0;i<=n;i++) {
        o.add(rel.rightRelata(i).first());
      }
      return o;
    } else {
      throw new RuntimeException("sequence cannot be composed with other sequence");
    }
  }
  
  public Sequence hold(Rhythm r) {
    int n = r.getN();
    int k = r.getK();
    
    if(k != this.size()) throw new RuntimeException("Sequence::hold invalid argument");
    Sequence o = new Sequence();
    int current = this.get(this.size()-1);
    int j = 0;
    for(int i=0; i<n ;i++) {
      if(r.get(i)) current = this.get(j++);
      o.add(current);
    }
    return o;
  }
  
  public Sequence extract(Rhythm r) {
    Sequence o = new Sequence();
    
    for(int i=0; i<r.getN();i++) {
      if(r.get(i)) o.add(this.get(i));
    }
    return o;
  }
  
  public Sequence absoluteTimeConvolve(Rhythm r, Sequence impulse) {
    return this.hold(r).convolveWith(impulse).extract(r);
  }
  
  public Sequence convolveWith(Sequence impulse) {
    Sequence o = new Sequence();
    for(int i=0;i<this.size();i++) o.add(0);
    
    for(int i=0; i<o.size(); i++) {
      for(int j=0; j<impulse.size(); j++) {
        int v = this.get(i)*impulse.get(j);
        o.set((i+j) % o.size(), 
          o.get((i+j) % o.size()) + v);
      }
    }
    return o;
  }
  
  public Sequence juxtapose(Sequence j) {
    Sequence s = new Sequence(this);
    s.addAll(j);
    return s;
  }
  
  public Sequence wrapseq(int p_min, int p_amp) {
    if(p_amp == 0) throw new RuntimeException("Sequence.wrapseq: amp must be non-zero");
    int d = 1;
    if(p_amp<0){
      d = -1;
      p_amp = Math.abs(p_amp);
    }
    
    Sequence allowed = stair(p_min, p_amp,d);
    int asz = allowed.size();
    
    int min = this.getMin();
    int max = this.getMax();
    int minmap = p_min;
    while(minmap > min) minmap -= asz;
    int maxmap = p_min + p_amp;
    while(maxmap < max) maxmap += asz;
    
    TreeMap<Integer,Integer> map = new TreeMap<>();
    int acc = 0;
    for(int i=minmap;i<=maxmap;i++) {
      map.put(i, allowed.get(Numbers.correctMod(acc++, allowed.size())));
    }
    return this.map(map);
  }
  
  public Sequence bounceseq(int p_min, int p_amp) {
    if(p_amp == 0) throw new RuntimeException("Sequence.bounceseq: amp must be non-zero");
    int d = 1;
    
    if(p_amp<0) {
      d = -1;
      p_amp = Math.abs(p_amp);
    }
    
    Sequence allowed = tri(p_min, p_amp, d);
    
    int asz = allowed.size();
    
    int min = this.getMin();
    int max = this.getMax();
    int minmap = p_min;
    while(minmap > min) minmap -= asz;
    int maxmap = p_min + p_amp;
    while(maxmap < max) maxmap += asz;
    
    TreeMap<Integer,Integer> map = new TreeMap<>();
    int acc = 0;
    for(int i=minmap;i<=maxmap;i++) {
      map.put(i, allowed.get(acc++%asz));
    }
    return this.map(map);
  }
  
  public Sequence flip()
  {
    return subFlip(this, this.getMin(), this.getMax());
  }

  private static Sequence subFlip(Sequence s, int l, int h)
  {
    Sequence o = new Sequence();
    for(int i=0;i<s.size();i++)
    { 
      o.add(h-(s.get(i)-l));
    }
    return o;
  }
  
  public TreeMap<Integer, Integer> frequencyMap() {
    TreeMap<Integer, Integer> o = new TreeMap<>();
    for(int i: this.distinct()) {
      o.put(i, this.count(i));
    }
    return o;
  }
  
  public double entropy() {
    double o = 0.0;
    
    TreeMap<Integer, Integer> freqs = this.frequencyMap();
    
    for(int i: freqs.keySet()) {
      double p = ((double)freqs.get(i))/((double)this.size());
      o += p*Math.log(p);
    }
    
    o = (o==0.0?0.0:-o);
    return o;
  }
  
  public int sumOfPairwiseDistances() {
    int o = 0;
    
    for(int i=0;i<this.size();i++) {
      for(int j=i;j<this.size();j++) {
        o+= Math.abs(this.get(i) - this.get(j));
      }
    }
    return o;
  }
  
  public Sequence signs() {
    Sequence s = new Sequence();
    for(int i=0; i < this.size(); i++) {
      if(this.get(i) > 0) s.add(1);
      else if(this.get(i) < 0) s.add(-1);
      else s.add(0);
    }
    return s;
  }
  
  /**
   * Returns a sequence where each element has been multiplied by k.
   * 
   * @param k
   * @return Another sequence with elements being k*element
   */
  public Sequence multiply(int k) {
    Sequence s = new Sequence();

    for (int i = 0; i < this.size(); i++) {
      s.add(this.get(i) * k);
    }

    return s;
  }

  /**
   * Each row i of the output matrix correspond to a distinct value of the sequence. Each cell i,j
   * set to true means that the value at position j in the sequence is the i'th biggest.
   * 
   * @return
   */
  public Matrix<Boolean> decompose() {
    Map<Integer, Integer> om = mapOrdinalsUnipolar();
    int m = distinct().size();
    int n = this.size();
    Matrix<Boolean> mat = new Matrix<Boolean>(m, n, false);
    for (int j = 0; j < n; j++) {
      mat.set(om.get(get(j)), j, true);
    }
    return mat;
  }
  
  /**
   * Returns a sequence where each element has been raised to a power.
   * 
   * @param power
   * @return Another sequence with elements being pow(element, power)
   */
  public Sequence powExp(Integer power) {
    Sequence s = new Sequence();

    for (int i = 0; i < this.size(); i++) {
      s.add(checkedPow(this.get(i), power));
    }

    return s;
  }

  public Sequence addToAll(int n) {
    Sequence output = new Sequence();
    for(Integer i : this) {
      output.add(i+n);
    }
    return output;
  }
  
  /**
   * Returns a sequence where each element the result of applying power with base as the base and
   * the original element as the exponent.
   * 
   * @param base
   * @return Another sequence with elements being pow(base, original_element)
   */
  public Sequence powBase(Integer base) {
    Sequence s = new Sequence();

    for (int i = 0; i < this.size(); i++) {
      s.add(checkedPow(base, this.get(i)));
    }

    return s;
  }
  
  public Sequence mapWithNextPermutation(){
    Set<Integer> d = distinct();
    int dsz = d.size();

    TreeBidiMap<Integer, Integer> uni = new TreeBidiMap<Integer, Integer>(
        mapOrdinalsUnipolar());
    int k=0;
    int i = 0;
    Integer[] permutation = new Integer[dsz];
    while(!d.isEmpty()) {
      int v = get(i++);
      if(d.contains(v)) {
        d.remove(v);
        int r = uni.get(v);
        permutation[k++] = r;
      }
    }
    
    Integer[] p1 = PermutationEnumeration.getNext(permutation);
    if(p1 == null) {
      return null;
    }
    Map<Integer,Integer> m = new TreeMap<Integer,Integer>();
    for(int j=0;j<dsz;j++){
      if(p1[j] != permutation[j]){
        m.put(uni.getKey(permutation[j]),uni.getKey(p1[j]));
      }
    }
    
    return map(m);
    
  }

  /**
   * Reverses a list.
   * 
   * @return Another sequence which is the reverse of this one.
   */
  public Sequence reverse() {
    Sequence r = new Sequence();

    for (int i = this.size() - 1; i >= 0; i--) {
      r.add(this.get(i));
    }

    return r;

  }

  /**
   * Rotates sequence 1 position to the right.
   * 
   * @return A new sequence.
   */
  public Sequence rotateRight() {
    return this.rotate(1);
  }

  /**
   * Rotates sequence 1 position to the left.
   * 
   * @return A new sequence.
   */
  public Sequence rotateLeft() {
    return this.rotate(-1);
  }

  /**
   * Rotates sequence n positions to the right. If n is negative, rotates -n positions to the left.
   * 
   * @param n may be any integer, even negative
   * @return A new sequence.
   */
  public Sequence rotate(int n) {
    return Sequence.from(CollectionUtils.rotate(this, n));
  }

  public Integer[] getArray(){
    Integer[] o = new Integer[size()];
    for(int i=0;i<size();i++) {
      o[i] = get(i);
    }
    return o;
  }
  /**
   * Finds the minimum of this sequence.
   * 
   * @return
   */
  public int getMin() {
    int m = this.get(0);
    for (int i = 0; i < this.size(); i++) {
      if (this.get(i) < m) {
        m = this.get(i);
      }
    }
    return m;
  }

  /**
   * Finds the maximum of this sequence.
   * 
   * @return
   */
  public int getMax() {
    int m = this.get(0);
    for (int i = 0; i < this.size(); i++) {
      if (this.get(i) > m) {
        m = this.get(i);
      }
    }
    return m;
  }

  /**
   * Calculates the mean of the sequence.
   * 
   * @return
   */
  public double getMean() {
    Iterator<Integer> i = this.iterator();
    double s = 0;
    while (i.hasNext()) {
      s += i.next();
    }
    return s / (double) this.size();
  }

  /**
   * Calculates the standard deviation of the sequence.
   * 
   * @return
   */
  public double getStdDev() {
    double m = getMean();
    double s = 0;
    Iterator<Integer> i = this.iterator();
    while (i.hasNext()) {
      s += Math.abs((double) i.next() - m);
    }
    return s / (double) this.size();
  }

  public Sequence difference() {
    return Sequence.from(CollectionUtils.difference(this.toArray(new Integer[0])));
  }

  public Sequence cyclicalForwardDifference() {
    return Sequence.from(CollectionUtils.cyclicalForwardDifference(this.toArray(new Integer[0])));
  }
  public Sequence cyclicalBackwardDifference() {
    return Sequence.from(CollectionUtils.cyclicalBackwardDifference(this.toArray(new Integer[0])));
  }
  
  public Sequence antidifference(int k) {
    return Sequence.from(CollectionUtils.antidifference(this.toArray(new Integer[0]), k));
  }
  public Sequence cyclicalForwardAntidifference(int k) {
    return Sequence.from(CollectionUtils.cyclicalForwardAntidifference(this.toArray(new Integer[0]), k));
  }
  public Sequence cyclicalBackwardAntidifference(int k) {
    return Sequence.from(CollectionUtils.cyclicalBackwardAntidifference(this.toArray(new Integer[0]), k));
  }
  /**
   * Construct a sequence from a list
   * 
   * @param p_arr
   */
  public Sequence(List<Integer> p_arr) {
    super(p_arr);
  }

  /**
   * Construct a sequence from an array
   * 
   * @param p_arr
   */
  public Sequence(Integer[] p_arr) {
    this(Arrays.asList(p_arr));
  }

  public Sequence(int[] p_arr) {
    this();
    for (int e : p_arr) {
      this.add(e);
    }
  }

  /**
   * Empty constructor.
   */
  public Sequence() {
    super();
  }

  
  
  /**
   * Returns a sequence constructed from a list.
   * 
   * @param p_arr
   * @return
   */
  public static Sequence from(List<Integer> p_arr) {
    return new Sequence(p_arr);
  }

  /**
   * Returns a sequence constructed from an array.
   * 
   * @param p_arr
   * @return
   */
  public static Sequence from(Integer[] p_arr) {
    return new Sequence(Arrays.asList(p_arr));
  }

  /**
   * 
   * @return a TreeSet with all distinct elements of this sequence.
   */
  public ComparableSet<Integer> distinct() {
    return new ComparableSet<Integer>(this);
  }

  /**
   * Counts the number of occurences of n in the sequence.
   * 
   * @param n
   * @return
   */
  public int count(int n) {
    int k = 0;
    for (int i = 0; i < this.size(); i++) {
      if (this.get(i).equals(n)) {
        k++;
      }
    }
    return k;
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public int compareTo(Sequence o) {
    return (new IterableComparator<Integer>()).compare(this, o);
  }

  @Override
  public String toString() {
    return Joiner.on(' ').join(this);
  }

  /**
   * @return The sum of the sequence.
   */
  public int sum() {
    Integer output = 0;
    for (int i = 0; i < this.size(); i++) {
      output += this.get(i);
    }
    return output;
  }

  /**
   * @return The offset of the first rotation that matches the sequence. Greater than one and at
   *         most the length.
   */
  public int getPeriod() {
    int p = this.size();

    for (int i = 1; i < this.size(); i++) {
      if (this.compareTo(this.rotate(i)) == 0) {
        p = i;
        break;
      }
    }
    return p;
  }
  
  public Sequence circularHoldNonZero() {
    Sequence o = new Sequence();
    for(int i:this) o.add(i);
    int last_non_zero = -1;
    for(int i=o.size();i>=0;i--) {
      if(o.get(i%o.size()) != 0) {
        last_non_zero = i%o.size();
        break;
      }
    }
    
    if(last_non_zero == -1) {
      for(int i=0;i<o.size();i++) {
        o.set(i, 1);
      }
      return o;
    }
    
    for(int i=0;i<o.size();i++) {
      if(o.get(i) == 0) {
        int k = 1;
        while(o.get((i-k+o.size())%o.size()) == 0) {
          k++;
        }
        int v = o.get((i-k+o.size())%o.size());
        for(int j=0;j<k;j++) {
          o.set(((i-j)+o.size())%o.size(), v);
        }
      }
    }
    return o;
  }
  
  /**
   * @return The difference between max and min + 1.
   */
  public int rangeSize() {
    int min = this.getMin();
    int max = this.getMax();
    return max - min + 1;
  }

  /**
   * Computes the map that maps elements of this sequence to their ordinal rank. The
   * smallest element becomes 0, second smallest 1, etc...
   * 
   * @return A map from distinct elements of the sequence to their rank. The smallest element
   *         becomes 0, second smallest 1, etc...
   */
  public TreeBidiMap<Integer, Integer> mapOrdinalsUnipolar() {
    TreeBidiMap<Integer, Integer> o = new TreeBidiMap<Integer, Integer>();
    o.put(0, 0);
    TreeSet<Integer> d =  this.distinct();
    
    int k = 0;
    for(Integer i : d) {
      o.put(i, k);
      k++;
    }
    return o;
  }

  /**
   * Converts the elements of this sequence to unipolar ordinals.
   * 
   * @see name.NicolasCoutureGrenier.Maths.DataStructures.Sequence#mapOrdinalsUnipolar()
   * @return
   */
  public Sequence asOrdinalsUnipolar() {
    return this.map(mapOrdinalsUnipolar());
  }

  /**
   * Computes the map that maps elements of this sequence to their bipolar ordinal rank. A map from
   * distinct elements of the sequence to their rank. Negative and positive elements maps
   * respectively to negative and positive. Absolute ordinal values correspond to their order in the
   * sorted list of absolute values.
   * 
   * @see name.NicolasCoutureGrenier.Maths.DataStructures.Sequence#mapOrdinalsBipolar()
   * @return
   */
  public TreeBidiMap<Integer, Integer> mapOrdinalsBipolar() {
    TreeBidiMap<Integer, Integer> o = new TreeBidiMap<Integer, Integer>();
    o.put(0, 0);
    TreeSet<Integer> d =  this.distinct();
    TreeSet<Integer> abs_d = new TreeSet<Integer>();
    
    for(Integer i : d) {
      abs_d.add(Math.abs(i));
    }
    abs_d.remove(0);
    int k = 1;
    for(Integer i: abs_d) {
      o.put(i, k);
      o.put(-i, -k);
      k++;
    }
    return o;
  }

  /**
   * Converts the elements of this sequence to bipolar ordinals.
   * 
   * @see name.NicolasCoutureGrenier.Maths.DataStructures.Sequence#mapOrdinalsBipolar()
   * @return
   */
  public Sequence asOrdinalsBipolar() {
    return this.map(mapOrdinalsBipolar());
  }

  public TreeMap<Integer, Sequence> getIntervalVectors() {
    return CollectionUtils.calcIntervalVector(this);
  }

  public TreeMap<Integer, Rhythm> getRhythms() {

    TreeMap<Integer, Rhythm> output = new TreeMap<Integer, Rhythm>();
    TreeSet<Integer> t = new TreeSet<Integer>();

    t.addAll(this);

    Iterator<Integer> i = t.iterator();

    while (i.hasNext()) {
      int v = i.next();
      Boolean[] b = new Boolean[this.size()];
      for (int j = 0; j < this.size(); j++) {
        b[j] = this.get(j).equals(v);
      }
      output.put(v, Rhythm.buildRhythm(b));
    }
    return output;
  }
  
  public Sequence map(Sequence s) {
    TreeMap<Integer, Integer> t = new TreeMap<>();
    for(int i=0;i<s.size();i++) {
      t.put(i, s.get(i));
      if(i > 0) t.put(-i, -s.get(i));
    }
    return Sequence.map(this, t);
  }
  
  public Sequence map(Map<Integer, Integer> i) {
    return Sequence.map(this, i);
  }

  public Sequence permutate(Sequence s) {
    if(s.distinct().size() != this.size() || s.getMin() != 0 || s.getMax() != this.size()-1) {
      throw new RuntimeException("Invalid permutation");
    }
    Sequence o = new Sequence();
    for(int i=0;i<s.size();i++) {
      o.add(this.get(s.get(i)));
    }
    return o;
  }
  
  /**
   * Inserts element v in a random position of this sequence.
   * 
   * @param v
   */
  public void addAtRandom(Integer v) {
    int p = RandomNumberGenerator.nextInt(size() + 1);
    this.add(p, v);
  }

  /**
   * @return A copy of this sequence.
   */
  public Sequence copy() {
    Sequence o = new Sequence();
    for (int i = 0; i < this.size(); i++) {
      o.add(this.get(i));
    }
    return o;
  }

  /**
   * Returns a new sequence with n elements randomly removed from it.
   * 
   * @param n
   * @return
   */
  public Sequence rndRemove(int n) {
    Sequence o = this.copy();
    if (n <= 0) {
      return o;
    }

    int p = 0;
    for (int i = 0; i < n; i++) {
      p = RandomNumberGenerator.nextInt(o.size());
      o.remove(p);
    }

    return o;

  }

  /**
   * Returns a new sequence with n elements randomly added to it.
   * 
   * @param n
   * @return
   */
  public Sequence rndAdd(int n) {
    Sequence o = this.copy();
    if (n <= 0) {
      return o;
    }

    int m = (int) Math.round(o.getMean());
    int s = (int) Math.round(o.getStdDev());

    int sz = o.size();
    int v = 0;
    int p = 0;
    for (int i = 0; i < n; i++) {
      v = (int) Math.round((RandomNumberGenerator.nextGaussian() * s) + m);
      p = RandomNumberGenerator.nextInt(sz + 1 + i);
      o.add(p, v);
    }
    return o;

  }

  /**
   * Applies a mapping on a sequence.
   * 
   * @param s The sequence
   * @param m The map
   * @return
   */
  public static Sequence map(Sequence s, Map<Integer, Integer> m) {
    Sequence output = new Sequence();

    for (int x = 0; x < s.size(); x++) {
      output.add(m.get(s.get(x)));
    }
    return output;

  }

  public boolean existsMapTo(Sequence s) {
    return mapTo(s) != null;
  }

  /**
   * @param s The sequence to which a map is seeked
   * @return A map that map this sequence into sequence s or null if it does not exists.
   */
  public ComparableMap<Integer, Integer> mapTo(Sequence s) {
    ComparableMap<Integer, Integer> mapping = new ComparableMap<Integer, Integer>();

    if (size() != s.size()) {
      return null;
    }
    int n = size();
    int a;
    int b;
    for (int i = 0; i < n; i++) {
      a = get(i);
      b = s.get(i);
      if (mapping.containsKey(a) && mapping.get(a) != b) {
        mapping.clear();
        return null;
      }
      if (!mapping.containsKey(a)) {
        mapping.put(a, b);
      }
    }
    return mapping;
  }

  /**
   * Returns the rotation offset that turns a into b if it exists.
   * 
   * @param a Sequence
   * @param b Sequence
   * @return Integer i such that a.rotate(i) == b or null if it does not exist
   */
  public static Integer equivalenceShift(Sequence a, Sequence b) {
    if (a.size() != b.size()) {
      return null;
    }

    for (int i = 0; i < a.size(); i++) {
      if (b.equals(a.rotate(i))) {
        return i;
      }
    }
    return null;
  }

  /**
   * @param a Sequence
   * @param b Sequence
   * @return True if there exists a rotation of a that is equivalent to b.
   */
  public static boolean equivalentUnderRotation(Sequence a, Sequence b) {
    return equivalenceShift(a, b) != null;
  }
  
  

  /**
   * Compares sequences backwards from the end and return the opposite of the result of comparison.
   */
  public static class ReverseComparator implements Comparator<Sequence> {
    /**
     * Compares sequences backwards from the end and return the opposite of the result of
     * comparison.
     */
    @Override
    public int compare(Sequence o1, Sequence o2) {
      return (-1) * IterableComparator.reverseCompare(o1, o2);
    }

  }

  public static Sequence genRnd(int vl, int amp, int sum, int maxamp, boolean ex0) {
    return Sequence.genRnd(vl, amp,sum, maxamp, ex0, null);
  }

  /**
   * Generates a random sequence.
   * 
   * @param vl the number of elements
   * @param amp the maximum difference between 2 successive elements
   * @param sum the las value of the sequence
   * @param maxamp the maximum amplitude of the sequence
   * @param ex0 exclude zero, that means values subsequent values will all be different
   * @return
   */
  public static Sequence genRnd(int vl, int amp, int sum, int maxamp, boolean ex0, Integer start) {
    if (vl <= 1 || amp < 1 || maxamp < 2 || (ex0 && maxamp < 2) || (Math.abs(sum) >= maxamp)
        || (amp == 1 && ex0 && (vl % 2) == 0 && (sum % 2) == 0)) {
      throw new RuntimeException("Sequence : genRnd check parameters.");
    }

    //vl = vl - 1;
    Sequence t = new Sequence();

    int currval = sum;

    t.add(currval);

    Sequence o = new Sequence();
    ArrayList<Integer> possibles = new ArrayList<Integer>();
    outside: while (true) {

      if (t.size() < (vl)) {

        inside: while (true) {
          possibles.clear();

          for (int i = -amp; i <= amp; i++) {
            if ((Math.abs(currval + i) < maxamp) && (i != 0 || !ex0)) {
              possibles.add(i);
            }
          }
          if (possibles.isEmpty()) {
            o.clear();
            t.clear();
            currval = sum;
            t.add(currval);
            break inside;
          }
          double[] weights = new double[possibles.size()];
          for(int i=0;i<weights.length;i++) {
            weights[i] = 1.0/(double)((1+t.count(currval + possibles.get(i)))*Numbers.factorial(Math.abs(possibles.get(i))));
          }
          
          t.add(possibles.size() == 1 ? possibles.get(0) : CollectionUtils.chooseAtRandomWithWeights(possibles, weights) + currval);

          currval = t.get(t.size() - 1);

          break inside;
        }
      } else {
        o = t.reverse();
        if (start == null || (start != null && o.get(0) == start)) {
          break outside;
        } else {
          t.clear();
          o.clear();
          currval = sum;
          t.add(currval);
        }
      }
    }
    return o;
  }
  
  public static Sequence genRndOnRhythm(Rhythm R, int amp, int maxamp, boolean addF, boolean addS) {
    Sequence o = new Sequence();
    int n = R.getN();
    Sequence CR = R.getComposition().asSequence();
    TreeMap<Integer, Integer> seg = new TreeMap<>();
    //TreeSet<HeterogeneousPair<Integer,Integer>> P = new TreeSet<>();
    TreeSet<Integer> F = Numbers.factors(n);
    F.remove(1); F.remove(n);
    TreeMap<Integer,Integer> I = new TreeMap<>();
    TreeMap<Integer,HeterogeneousPair<Integer,Integer>> h = new TreeMap<>();
    TreeMap<HeterogeneousPair<Integer,Integer>, TreeMap<Integer,Sequence>> s = new TreeMap<>();
    TreeMap<Integer, TreeMap<Integer, Integer>> coordf = new TreeMap<>();
    TreeMap<Integer,Sequence> epsf = new TreeMap<>();
    
    int k = CR.size();
    int d=0;
    int c=0;
    int acc=0;
    TreeMap<Integer, Integer> cs = new TreeMap<>();
    TreeMap<Integer, Integer> V = new TreeMap<>();
    int nbf = RandomNumberGenerator.nextInt(F.size()%2 == 0 ? F.size()/2 : (F.size()+1)/2);
    Sequence F2 = new Sequence();
    
    for(int i=0; i<nbf;i++) {
      double[] weights = new double[F.size()];
      int j = 0;
      for(int _f : F) { weights[j++] = 1.0/Math.exp((double) _f); }
      int f = F.size() == 1 ? F.first() : CollectionUtils.chooseAtRandomWithWeights(F, weights);
      int kp = n / f;
      F.remove(f);
      F.remove(kp);
      F2.add(f);
      int _sum = RandomNumberGenerator.nextInt(maxamp);
      epsf.put(f, Sequence.genRnd(kp, amp, _sum, maxamp, true));
    }
    cs.put(0,0);
    V.put(0, CR.get(0));
    for(int i=0; i<k; i++) {
      if(i != 0 && CR.get(i) != CR.get(i-1)) {
        c++; 
        cs.put(c, 0);
        V.put(c, CR.get(i));
        d=0;
      }
      seg.put(i, c);
      cs.put(c, cs.get(c)+1);
      I.put(i, d++);
      acc += CR.get(i);
    }
    
    for(int i=0; i<F2.size();i++) {
      int f = F2.get(i);
      int kp = n / f;
      epsf.put(f, Sequence.genRnd(kp, amp, 0, maxamp, true));
      coordf.put(f, new TreeMap<>());
      acc = 0;
      for(int j=0; j<k;j++) {
        coordf.get(f).put(j, (int)Math.floor((double) acc / (double)f));
        acc += CR.get(j);
      }
    }
    
    Sequence seqepsc = new Sequence();
    for(int i=0; i<=c;i++) {
      
      HeterogeneousPair<Integer,Integer> pair = HeterogeneousPair.makeHeterogeneousPair(cs.get(i), V.get(i));
      h.put(i, pair);
      if(!s.containsKey(pair)) {
        s.put(pair, new TreeMap<>());
      }
    }
    
    Sequence _s0 = new Sequence();
    _s0.add(0);
    
    int szs = s.size();
    int __i=0;
    Sequence seqs = szs <= 1 ? _s0 : Sequence.genRnd(szs, amp, 0, maxamp, true);
    for(HeterogeneousPair<Integer,Integer> pair : s.keySet()) {
      
      Sequence sequence0 = new Sequence();
      sequence0.add(seqs.get(__i));
      
      int j=0;
      Sequence plainSeq = pair.getFirst() == 1 ? sequence0 : Sequence.genRnd(pair.getFirst(), amp, seqs.get(__i), maxamp, true);
      
      s.get(pair).put(j++, plainSeq);
      int pk = plainSeq.size();
      if(pk > 1) {
        s.get(pair).put(j++, plainSeq.reverse());
        TreeSet<Integer> pf = Numbers.factors(pk);
        pf.remove(pk);
        pf.remove(1);
        
        for(int _f : pf) {
          for(int u=_f;u<pk;u+=_f) {
            s.get(pair).put(j++, plainSeq.rotate(u));
            s.get(pair).put(j++, plainSeq.rotate(u).reverse());
          }
        }
      }
      __i++;
    }
    
    for(int i=0; i<=c;i++) {
      seqepsc.add(RandomNumberGenerator.nextInt(s.get(h.get(i)).size()));
    }
    
    for(int i=0;i<k;i++) {
      int v = 0;
      int _c = seg.get(i);
      if(addS) v += s.get(h.get(_c)).get(seqepsc.get(_c)).get(I.get(i));
      
      if(addF) {
        for(int j=0; j<nbf;j++) {
          int _f = F2.get(j);
          v += epsf.get(_f).get(coordf.get(_f).get(i));
        }
      }
      
      o.add(v);
    }
    return o;
  }

}
