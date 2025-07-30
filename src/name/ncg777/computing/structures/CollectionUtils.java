package name.ncg777.computing.structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

import com.google.common.base.Equivalence;
import com.google.common.base.Predicate;

import name.ncg777.maths.Composition;
import name.ncg777.maths.Matrix;
import name.ncg777.maths.Numbers;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.statistics.RandomNumberGenerator;

public class CollectionUtils {

  public static <A extends List<Integer>> Integer[] getPermutationFromDisjointCycles(TreeSet<A> cs) {
    TreeSet<Integer> t = new TreeSet<>();
    int sz = 0;
    
    for(var c : cs) {
      sz += c.size();
      t.addAll(c);
    }
    
    if(sz != t.size() || t.first() != 0 || t.last() != sz-1) {
      throw new RuntimeException("CollectionUtils::getPermutationFromDisjointCycles - invalid argument.");
    }
    
    Integer[] o = new Integer[sz];
    
    for(var c: cs) {
      for(int i=0; i<c.size();i++) {
        o[c.get(i)] = c.get((i+1)%c.size());
      }
    }
    return o;
  }
  
  public static Function<Integer,Integer> getPermutationFunction(Integer[] permutation) {
    return (i) -> permutation[i];
  }
  
  @SuppressWarnings("null")
  public static long getPermutationOrder(Integer[] permutation) {
    TreeSet<Sequence> cs = getPermutationAsDisjointCycles(permutation);
    
    Long o = null;
    
    for(var s: cs) {
      if(o == null) {o = Long.valueOf(s.size());}
      else { o = Numbers.lcm(o, s.size());}
    }
    return o;
  }
  
  public static TreeSet<Sequence> getPermutationAsDisjointCycles(Integer[] permutation) {
    TreeSet<Sequence> o = new TreeSet<>();
    Sequence s = new Sequence(permutation);
    
    var f = getPermutationFunction(permutation);
    
    while(!s.isEmpty()) {
      Sequence c = new Sequence();
      
      var initial = s.get(0);
      
      var current = initial;
      
      do {
        c.add(current);
        current = f.apply(current);
      } while(current != initial);
      s.removeAll(c);
      o.add(c);
    }
    return o;
  }
  
  /**
   * Returns the nth permutation (lexicographic order) of [0, 1, 2, ..., k]
   * where k is minimal so that k! > n and n >= 0.
   * For n < 0, returns the reverse of the corresponding permutation for -n.
   * Throws IllegalArgumentException if n is too large for the permutation size.
   */
  public static List<Integer> getPermutation(int n) {
      if (n == 0) return Arrays.asList(0);

      int absN = Math.abs(n);

      // Find minimal k such that k! > absN
      int k = 1;
      int fact = 1;
      while (fact <= absN) {
          k++;
          fact *= k;
      }
      k--; // overshot

      int totalPerms = fact / (k + 1) * (k + 1); // (k+1)!
      if (absN >= totalPerms) {
          throw new IllegalArgumentException("n is too large to generate a permutation of size " + (k + 1));
      }

      // Build Lehmer code
      List<Integer> lehmerCode = new ArrayList<>();
      int remaining = absN;
      fact /= (k + 1); // Start with k! for k+1 elements
      for (int i = k; i >= 1; i--) {
          lehmerCode.add(remaining / fact);
          remaining %= fact;
          if (i > 1) fact /= i;
      }

      // Build permutation from Lehmer code
      List<Integer> elements = new ArrayList<>();
      for (int i = 0; i <= k; i++) elements.add(i);

      List<Integer> permutation = new ArrayList<>();
      for (int code : lehmerCode) {
          permutation.add(elements.remove(code));
      }
      permutation.add(elements.get(0)); // add last remaining element

      // For negative n, reverse the permutation
      if (n < 0) Collections.reverse(permutation);

      return permutation;
  }
  
  public static int getPermutationNumber(List<Integer> perm) {
    int n = perm.size();
    List<Integer> elements = new ArrayList<>();
    for (int i = 0; i < n; i++) elements.add(i);

    int number = 0;
    int fact = 1;
    for (int i = 2; i <= n; i++) fact *= i; // n!
    fact /= n; // (n-1)!

    for (int i = 0; i < n - 1; i++) {
        int idx = elements.indexOf(perm.get(i));
        number += idx * fact;
        elements.remove(idx);
        if (i < n - 2) fact /= (n - 1 - i);
    }
    return number;
  }
  public static <
  A extends Comparable<? super A>,
  B extends Comparable<? super B>> 
  SortedSet<HeteroPair<A,B>> cartesianProduct(
  SortedSet<A> a, SortedSet<B> b){
    TreeSet<HeteroPair<A,B>> o = 
        new TreeSet<HeteroPair<A,B>>();
    for(A x : a){
      for(B y : b){
        o.add(HeteroPair.makeHeteroPair(x, y));
      }
    }
    return o;
  }
  public static <
  A extends Comparable<? super A>,
  B extends Comparable<? super B>> 
  List<HeteroPair<A,B>> cartesianProduct(
  Iterable<A> a, Iterable<B> b){
    List<HeteroPair<A,B>> o = 
        new ArrayList<HeteroPair<A,B>>();
    for(A x : a){
      for(B y : b){
        o.add(HeteroPair.makeHeteroPair(x, y));
      }
    }
    return o;
  }
  /**
   * Enumerates all range^n tuples of length n with values in [0 ... range-1] in the rows of a
   * matrix.
   * 
   * @param n Natural of elements
   * @param range elements are in [0 ... range-1] if range > 0 or [-(range-1) ... 0]
   * @return Matrix<Integer>
   */
  public static Matrix<Integer> enumerate(int n, int range) {
    if (n < 1 || range == 0) {
      throw new RuntimeException("CollectionUtils::enumerate Invalid arguments");
    }
    int sign = 0;
    if (range > 0) {
      sign = 1;
    }
    if (range < 0) {
      sign = -1;
    }
    if (n == 1) {
      Matrix<Integer> m0 = new Matrix<Integer>(Math.abs(range), 1, 0);
      for (int i = 0; i < Math.abs(range); i++) {
        m0.set(i, 0, i * sign);
      }
      return m0;
    } else {
      Matrix<Integer> m0 = enumerate(n - 1, range);
      int nbRowM0 = m0.rowCount();

      int mr = Math.abs(range) * m0.rowCount();
      int nr = m0.columnCount() + 1;

      Matrix<Integer> m1 = new Matrix<Integer>(mr, nr, 0);

      ArrayList<Integer> cz = new ArrayList<Integer>(Math.abs(range) * m0.rowCount());
      for (int i = 0; i < Math.abs(range); i++) {
        m1.setBlock(m0, i * nbRowM0, 0);
        for (int j = 0; j < m0.rowCount(); j++) {
          cz.add(i * sign);
        }
      }
      m1.setColumn(nr - 1, cz);
      return m1;
    }

  }

  /**
   * Filters a set in place using a predicate.
   * 
   * @param s Set<T>
   * @param p Predicate<? super T>
   */
  public static <T> void filter(Set<T> s, Predicate<? super T> p) {
    ArrayList<T> x = new ArrayList<T>();
    Iterator<T> i = s.iterator();

    while (i.hasNext()) {
      T a = i.next();
      if (p.apply(a)) {
        x.add(a);
      }
    }

    s.retainAll(x);
  }

  /**
   * Chooses an element in the n elements after the element currently pointed to by the iterator.
   * 
   * @param i Iterator<T>
   * @param n int range to consider after the iterator
   * @return T element.
   */
  public static <T> T chooseAtRandom(Iterator<T> i, int n) {
    if (i == null || n <= 0) {
      throw new RuntimeException("CollectionUtils::chooseAtRandom Invalid arguments");
    }

    T o = null;
    int k = 0;
    int r = RandomNumberGenerator.nextInt(n);

    while (k <= r) {
      o = i.next();
      k++;
    }

    return o;
  }
  
  public static <T> T chooseAtRandomWithWeights(Iterator<T> i, int n, double[] weights) {
    if (i == null || n <= 0 || weights == null || weights.length != n) {
      throw new RuntimeException("CollectionUtils::chooseAtRandom Invalid arguments");
    }
    
    double[] normalizedWeights = new double[n];
    double sum = 0.0;
    double sum_normalized = 0.0;
    for(int j=0; j<n;j++) {
      sum += weights[j];
    }
    for(int j=0; j<n;j++) {
      if(j == n-1) {
        normalizedWeights[j] = 1.0 - sum_normalized;
      } else {
        normalizedWeights[j] = weights[j]/sum;
        sum_normalized += normalizedWeights[j];  
      }
    }
    
    double acc = 0.0;
    
    T o = null;
    int k = 0;
    double r = RandomNumberGenerator.nextDouble();

    while (i.hasNext()) {
      o = i.next();
      acc += normalizedWeights[k];
      if(r < acc) break;
      k++;
    }
    
    return o;
  }

  /**
   * Chooses a element at random from a collection.
   * 
   * @param t Collection<T>
   * @return T chosen element
   */
  public static <T> T chooseAtRandom(Collection<T> t) {
    return chooseAtRandom(t.iterator(), t.size());
  }
  
  public static <T> T chooseAtRandomWithWeights(Collection<T> t, double[] weights) {
    return chooseAtRandomWithWeights(t.iterator(), t.size(), weights);
  }
  
  public static <T> T chooseAtRandom(ArrayList<T> t) {
    if(t == null || t.size()==0) return null;
    return(t.get(RandomNumberGenerator.nextInt(t.size())));
  }
  
  /**
   * Partitions a set into equivalence classes using an equivalence.
   * 
   * @param s Set<T>
   * @param e Equivalence<T>
   * @return List<L
   */
  public static <T> List<Set<T>> partition(Set<T> s, Equivalence<T> e) {
    LinkedList<Set<T>> o = new LinkedList<Set<T>>();
    LinkedList<T> t = new LinkedList<T>();
    t.addAll(s);

    while (!t.isEmpty()) {
      T elem = t.pollFirst();

      boolean found = false;

      for (Set<T> i : o) {
        if (e.equivalent(i.iterator().next(), elem)) {
          i.add(elem);
          found = true;
          break;
        }
      }

      if (!found) {
        Set<T> n = new HashSet<T>();
        n.add(elem);
        o.addLast(n);
      }

    }

    return o;
  }

  /**
   * Tests whether the map is bijective.
   * 
   * @param m Map<T,U>
   * @return
   */
  public static <T, U> boolean mapIsBijective(Map<T, U> m) {
    return (m.keySet().size() == (new HashSet<U>(m.values())).size());
  }

  /**
   * Rotates the array n positions to the right.
   * 
   * @param arr
   * @param n can be negative and even greater than the size of the array
   * @return
   */
  public static <T> ArrayList<T> rotate(ArrayList<T> arr, int n) {
    ArrayList<T> c = new ArrayList<T>();
    c.addAll(arr);

    int m = n;

    while (m < 0) {
      m += c.size();
    }
    while (m > c.size()) {
      m -= c.size();
    }

    for (int i = 0; i < c.size(); i++) {
      c.set(i, arr.get((i + c.size() - m) % c.size()));
    }

    return c;

  }

  /**
   * Counts the number of occurrences of k in array a.
   * 
   * @param k
   * @param a
   * @return
   */
  public static <T> Integer countkins(T k, T[] a) {
    int output = 0;

    for (int i = 0; i < a.length; i++) {
      if (a[i].equals(k)) {
        output++;
      }
    }
    return output;
  }

  /**
   * Element-wise comparison of lists.
   * 
   * @param a
   * @param b
   * @return
   */
  public static <T> Boolean arrayEquals(List<T> a, List<T> b) {
    if (a.size() != b.size()) {
      return false;
    }

    for (int i = 0; i < a.size(); i++) {
      if (!a.get(i).equals(b.get(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Permutation p is used as a map and applied to s.
   * 
   * @param s an array
   * @param p a permutation starting at 0
   * @return
   */
  public static Integer[] mapUsingPermutation(Integer[] s, Integer[] p) {
    TreeMap<Integer, Integer> i = new TreeMap<Integer, Integer>();

    for (int x = 0; x < p.length; x++) {
      i.put(x, p[x]);
    }

    return map(s, i);

  }
  /**
   * Inverts a bijective map.
   * 
   * @param map
   * @throws RuntimeException When the map is not bijective.
   * @return The inverted map.
   */
  public static <U,T> Map<U,T> invertMap(Map<T,U> map){
    if(!mapIsBijective(map)){
      throw new RuntimeException("The map is not bijective; it cannot be inverted.");
    }
    Map<U,T> o = new TreeMap<U,T>();
    for(Map.Entry<T, U> e: map.entrySet()){
      o.put(e.getValue(), e.getKey());
    }
    return o;
  }
  
  /**
   * Apply map i (Integer -> Integer) to array s (Integers)
   * 
   * @param s array
   * @param i map
   * @return
   */
  public static Integer[] map(Integer[] s, TreeMap<Integer, Integer> i) {
    Integer[] output = new Integer[s.length];

    for (int x = 0; x < s.length; x++) {
      output[x] = i.containsKey(s[x]) ? i.get(s[x]) : s[x];
    }
    return output;

  }

  /**
   * Natural of distict values in s
   * 
   * @param s
   * @return
   */
  public static Integer sizeOfCodomain(Integer[] s) {
    TreeSet<Integer> t = new TreeSet<Integer>();
    t.addAll(Arrays.asList(s));
    return t.size();
  }

  /**
   * Calculates, for each possible distance in terms of positions in the array, the number of pairs
   * of true values separated by this distance. That's the interval vector; distances being the
   * intervals. The range of possible distances is half the size of the array. When the size is
   * even, the largest possible interval is counted twice, so it must be divided by 2.
   * 
   * @param input : an array of booleans
   * @return
   */
  public static Sequence calcIntervalVector(Boolean[] input) {
    int n = input.length;
    int m = n / 2;
    Sequence s = new Sequence();

    for (int i = 1; i <= m; i++) {
      int k = 0;
      for (int j = 0; j < n; j++) {
        if (input[j] && input[(i + j) % n]) {
          k++;
        }
      }
      if (i == m && n % 2 == 0) {
        k = k / 2;
      }
      s.add(k);
    }
    return s;
  }

  public static Sequence calcIntervalVector(BitSet input, int n) {
    int m = n / 2;
    Sequence s = new Sequence();

    for (int i = 1; i <= m; i++) {
      int k = 0;
      for (int j = 0; j < n; j++) {
        if (input.get(j) && input.get((i + j) % n)) {
          k++;
        }
      }
      if (i == m && n % 2 == 0) {
        k = k / 2;
      }
      s.add(k);
    }
    return s;
  }

  /**
   * For each distinct value in the input, the interval vector is calculated.
   * 
   * @param input
   * @return
   */
  public static TreeMap<Integer, Sequence> calcIntervalVector(Sequence input) {

    TreeMap<Integer, Sequence> output = new TreeMap<Integer, Sequence>();
    TreeSet<Integer> t = new TreeSet<Integer>();

    t.addAll(input);

    Iterator<Integer> i = t.iterator();

    while (i.hasNext()) {
      int v = i.next();
      Boolean[] b = new Boolean[input.size()];
      for (int j = 0; j < input.size(); j++) {
        b[j] = input.get(j).equals(v);
      }
      output.put(v, calcIntervalVector(b));
    }
    return output;
  }

  /**
   * 
   * @param input
   * @return
   */
  public static TreeMap<Integer, Sequence> calcIntervalVector(Integer[] input) {
    Sequence s = new Sequence();
    for (int i = 0; i < input.length; i++) {
      s.add(input[i]);
    }
    return calcIntervalVector(s);

  }

  public static Integer[] antidifference(Integer[] p_arr, int k) {
    Integer[] output = new Integer[p_arr.length + 1];

    output[0] = k;

    for (int i = 0; i < p_arr.length; i++) {
      output[i + 1] = output[i] + p_arr[i];
    }
    return output;
  }

  public static Integer[] difference(Integer[] p_arr) {
    Integer[] output = new Integer[p_arr.length - 1];

    for (int i = 1; i < p_arr.length; i++) {
      output[i - 1] = p_arr[i] - p_arr[i - 1];
    }
    return output;
  }

  public static Integer[] cyclicalDifference(Integer[] p_arr) {
    Integer[] output = new Integer[p_arr.length];

    for (int i = 0; i < p_arr.length; i++) {
      output[(i)%p_arr.length] = p_arr[(i + 1)%p_arr.length] - p_arr[(i+p_arr.length)%p_arr.length];
    }
    return output;
  }
  
  public static Integer[] cyclicalAntidifference(Integer[] p_arr, int k) {
    Integer[] output = new Integer[p_arr.length];
    
    output[p_arr.length-1] = k;
    
    for (int i = 0; i < p_arr.length; i++) {
      output[i] = output[(i-1 + p_arr.length) % p_arr.length] + p_arr[(i-1 + p_arr.length) % p_arr.length];
    }

    return output;
  }
  
  public static Integer[] reverse(Integer[] p_arr) {
    Integer[] output = new Integer[p_arr.length];
   
    for(int i=0;i<p_arr.length;i++) {
      output[p_arr.length-i-1] = p_arr[i];
    }
    return output;
  }
  public static <T> ArrayList<T> reverse(ArrayList<T> arr) {
    ArrayList<T> o = new ArrayList<>();
    for(int i=0;i<arr.size();i++) {
      o.add(arr.get(arr.size()-(i+1)));
    }
    return o;
  }
  public static String segmentationString(double[] cs, Composition p) {
    if (p.getTotal() != cs.length) {
      throw new IllegalArgumentException();
    }

    StringBuilder sb = new StringBuilder("");

    Sequence ps = p.asSequence();
    int k = ps.size();
    int offset = 0;

    for (int i = 0; i < k; i++) {
      sb.append("(");
      int x = ps.get(i);
      for (int j = 0; j < x; j++) {
        sb.append(Double.toString(cs[offset + j]));
        if (j < x - 1) {
          sb.append(" ");
        }
      }
      sb.append(")");
      if (i < k - 1) {
        sb.append(",\n");
      }
      offset += ps.get(i);
    }
    return sb.toString();
  }
  
  public static List<Integer> randomPermutation(int n){
    List<Integer> o = new ArrayList<Integer>();
    TreeSet<Integer> t = new TreeSet<Integer>();
    for(int i=0;i<n;i++){t.add(i);}
    for(int i=0;i<n;i++){
      Integer j = chooseAtRandom(t);
      t.remove(j);
      o.add(j);
    }
    return o;
  }
  
  public static <T> List<T> permutate(List<Integer> p, List<T> arr){
    int n = arr.size();
    ArrayList<T> o = new ArrayList<T>();
    for(int i=0; i<n; i++){o.add(arr.get(p.get(i)));}
    return o;
  }
  public static <T> List<T> permutateRandomly(List<T> arr){
    return permutate(randomPermutation(arr.size()), arr);
  }
  
}
