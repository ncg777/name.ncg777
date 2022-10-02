package name.ncg.Music;

import static name.ncg.Maths.DataStructures.Sequence.ReverseComparator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import name.ncg.Maths.Combination;
import name.ncg.Maths.Necklace;
import name.ncg.Maths.DataStructures.Sequence;


public class PCS12 extends Combination implements Serializable {

  private static final long serialVersionUID = 1L;

  static TreeMap<String, PCS12> ChordDict;
  static TreeMap<Combination, PCS12> ChordCombinationDict;

  Integer m_Order;
  Integer m_Transpose;

  static {
    GenerateMaps();
  }
  public int calcDistanceWith(PCS12 other) {
    
    int maxn = Math.max(this.getN(), other.getN());
    
    int acc = 0;
    for(int i=0; i<maxn;i++) {
      if(this.get(i) != other.get(i)) acc++;
    }
    
    return acc;
  }
  public boolean get(int i) {
    return super.get(i%getN());
  }
  
  public PCS12 transpose(int t) {
    return identify(this.rotate(t));
  }

  public PCS12 S12Permutate(Sequence s) {
    if(s.distinct().size() != 12 || s.getMin() != 0 || s.getMax() != 11) throw new RuntimeException("Invalid permutation");
    return PCS12.identify(Combination.fromBinarySequence(this.asBinarySequence().permutate(s)));
  }
  
  public static TreeMap<String, PCS12> getChordDict() {
    return ChordDict;
  }

  public static TreeSet<PCS12> getChords() {
    TreeSet<PCS12> output = new TreeSet<PCS12>();
    output.addAll(ChordDict.values());
    return output;
  }

  public static PCS12 parse(String input) {
    return ChordDict.get(input);
  }

  private PCS12(Set<Integer> p_s, Integer p_Order, Integer p_Transpose) {
    super(12, p_s);
    m_Order = p_Order;
    m_Transpose = p_Transpose;
  }

  public double getMean() {
    double s = 0.0;
    double k = getK();

    for (int i = 0; i < 12; i++) {
      if (get(i)) {
        s += i;
      }
    }
    return s / k;
  }

  @Override
  public String toString() {
    return String.format("%02d-%02d.%02d", this.getK(), m_Order, m_Transpose);
  }

  public String combinationString() {
    return (new Combination(this)).toString();
  }

  public Integer getOrder() {
    return m_Order;
  }

  public Integer getTranspose() {
    return m_Transpose;
  }
  
  public PCS12 intersect(PCS12 c){
    return identify(super.intersect(c));
  }
  
  public PCS12 minus(PCS12 c){
    return identify(super.minus(c));
  }

  static TreeSet<PCS12> generate() {
    Integer[] o = new Integer[12];

    for (int i = 0; i < 12; i++) {
      o[i] = 0;
    }

    TreeSet<PCS12> output = new TreeSet<PCS12>();
    TreeSet<Necklace> nck = Necklace.generate(12, 2);
    Necklace[] arr = new Necklace[nck.size()];

    int z = 0;
    Iterator<Necklace> i = nck.iterator();
    while (i.hasNext()) {
      arr[z] = i.next();
      z++;
    }

    Arrays.sort(arr, new ReverseComparator());

    for (int l = 0; l < z; l++) {
      Necklace n = arr[l];
      int period = n.getPeriod();

      int sz_tmp = 0;
      for (int k = 0; k < 12; k++) {
        if (n.get(k).equals(1)) {
          sz_tmp++;
        }
      }

      for (int j = 0; j < period; j++) {
        TreeSet<Integer> c = new TreeSet<Integer>();
        for (int k = 0; k < 12; k++) {
          if (n.get(k).equals(1)) {
            c.add(((12 - (k + 1)) + j) % 12);
          }
        }
        if (!c.isEmpty()) {
          output.add(new PCS12(c, o[sz_tmp - 1]+1, j));

        }
      }
      if (sz_tmp != 0) {
        o[sz_tmp - 1]++;
      }
    }

    output.add(new PCS12(new TreeSet<Integer>(), 0, 0));

    return output;
  }

  public PCS12 combineWith(PCS12 x) {
    return PCS12.identify(Combination.merge(this, x));
  }

  public PCS12 symmetricDifference(PCS12 y) {
    return PCS12.identify(super.symmetricDifference(y));
  }

  private static void GenerateMaps() {
    TreeSet<PCS12> t = generate();

    TreeMap<String, PCS12> output = new TreeMap<String, PCS12>();
    TreeMap<Combination, PCS12> output2 = new TreeMap<Combination, PCS12>();
    Iterator<PCS12> i = t.iterator();

    while (i.hasNext()) {
      PCS12 tmp = i.next();
      output.put(tmp.toString(), tmp);
      output2.put(tmp, tmp);
    }
    ChordDict = output;
    ChordCombinationDict = output2;
  }

  public static PCS12Sequence parseSeq(String input) {
    String[] s = input.split("\\s");
    PCS12Sequence output = new PCS12Sequence();
    for (int i = 0; i < s.length; i++) {
      output.add(parse(s[i]));
    }
    return output;
  }

  public static PCS12 empty() {
    return new PCS12(new TreeSet<Integer>(), 1, 0);
  }

  public static PCS12 identify(Combination input) {
    if (input.getN() != 12) {
      throw new IllegalArgumentException(
          "PCS12::IdentifyChord the combination is not bounded by 12");
    }

    if (input.isEmpty()) {
      return new PCS12(new TreeSet<Integer>(), 0, 0);
    }
    PCS12 o = ChordCombinationDict.get(input);

    if (o == null) {
      o = empty();
    }
    return o;
  }

  public static PCS12 identify(Sequence input) { return PCS12.identify(new TreeSet<Integer>(input));}
  public static PCS12 identify(Set<Integer> input) {
    if (input.isEmpty()) { return PCS12.empty();}

    boolean ex = false;
    Iterator<Integer> i = input.iterator();
    while (i.hasNext()) {
      Integer tmp = i.next();
      if (tmp == null || tmp > 11 || tmp < 0) {
        ex = true;
        break;
      }
    }
    if (input.size() > 12) {
      ex = true;
    }
    if (ex) {
      throw new IllegalArgumentException("PCS12::IdentifyChord Provided set is not a valid chord.");
    }

    return identify(new Combination(12, input));
  }
  
  public static String[] CommonScales() {
    ArrayList<String> o = new ArrayList<>();
    for(PCS12 ch : getChords()) {
      if(ch.getK() == 7) {
        if( ch.getOrder() == 26 || // "07-26.04", Major Locrian
            ch.getOrder() == 28 || // "07-28.11", Persian
            ch.getOrder() == 29 || // "07-29.06", Hungarian
            ch.getOrder() == 38 || // "07-38.11", Harmonic minor
            ch.getOrder() == 39 || // "07-39.11", Melodic minor
            ch.getOrder() == 42 || // "07-42.11", Harmonic major
            ch.getOrder() == 43)   // "07-43.11", Major
        {
          o.add(ch.toString());
        }
      }
      if(ch.getK() == 8) {
        if( ch.getOrder() == 35) // "08-35.00", Octatonic
        {
          o.add(ch.toString());
        }
      }
      if(ch.getK() == 12) {o.add(ch.toString());}
    }
    String[] oo = o.toArray(new String[0]);
    Arrays.sort(oo);
    return oo;
    
  }
}
