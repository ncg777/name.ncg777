package name.ncg.Music;

import java.io.Serializable;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import name.ncg.Maths.Combination;
import name.ncg.Maths.Necklace;
import name.ncg.Maths.DataStructures.Sequence;

import static com.google.common.math.IntMath.checkedPow;
import static name.ncg.Maths.DataStructures.CollectionUtils.*;
import static name.ncg.Maths.DataStructures.Sequence.ReverseComparator;

public class Rhythm12 extends Rhythm implements Serializable{

  private static final long serialVersionUID = 1L;

  static TreeMap<String, Rhythm12> RhythmDictByOctal;
  static TreeMap<String, Rhythm12> RhythmDictById;

  Integer m_Order;
  Integer m_Phase;

  String m_str;

  static {
    generateMaps();
  }

  public static Rhythm12 rotate(Rhythm12 r, int t) {
    return identifyRhythm12(Rhythm.rotate(r, t));
  }

  public static TreeMap<String, Rhythm12> getRhythm12Dict() {
    return RhythmDictByOctal;
  }

  public static TreeSet<Rhythm12> getRhythms12() {
    TreeSet<Rhythm12> output = new TreeSet<Rhythm12>();
    output.addAll(RhythmDictByOctal.values());
    return output;
  }

  public static Rhythm12 parseRhythm12Octal(String input) {
    return RhythmDictByOctal.get(input);
  }

  public static Rhythm12 parseRhythm12Id(String input) {
    return RhythmDictById.get(input);
  }

  private Rhythm12(Set<Integer> p_s, Integer p_Order, Integer p_Phase) {
    super(12, p_s);

    m_Order = p_Order;
    m_Phase = p_Phase;

    m_str = Rhythm12.toString(this);

  }

  public Rhythm asRhythm() {
    BitSet b = new BitSet(12);
    b.or(this);
    return new Rhythm(b, 12);
  }

  public static Rhythm12 fromRhythm(Rhythm r) {
    if(r.getN()!=12) throw new RuntimeException();
    Combination c = new Combination(12);
    c.or(r);
    return identifyRhythm12(c);
  }
  
  @Override
  public String toString() {
    if (m_str == null) {
      m_str = Rhythm12.toString(this);
    }
    return m_str;
  }
  
  public String getId(){
    return String.format("%02d-%03d.%02d", getK(),getOrder(),getPhase());
  }
  
  public static String toString(Combination c) {
    int msb = 0;
    int lsb = 0;

    for (int i = c.nextSetBit(0); i >= 0; i = c.nextSetBit(i + 1)) {
      if (i >= 6) {
        lsb += checkedPow(2, 5 - (i - 6));
      } else {
        msb += checkedPow(2, 5 - i);
      }
    }
    return String.format("%02o %02o", msb, lsb);
  }

  public Integer getOrder() {
    return m_Order;
  }

  public static Rhythm12 getZeroRhythm(){
    return parseRhythm12Octal("00 00");
  }
  public Integer getPhase() {
    return m_Phase;
  }



  static TreeSet<Rhythm12> Generate() {
    Integer[] o = new Integer[12];

    for (int i = 0; i < 12; i++) {
      o[i] = 0;
    }

    TreeSet<Rhythm12> output = new TreeSet<Rhythm12>();
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


      int sz_tmp = 0;
      for (int k = 0; k < 12; k++) {
        if (n.get(k).equals(1)) {
          sz_tmp++;
        }
      }

      for (int j = 0; j < 12; j++) {
        TreeSet<Integer> c = new TreeSet<Integer>();
        for (int k = 0; k < 12; k++) {
          if (n.get(k).equals(1)) {
            c.add(((12 - (k + 1)) + j) % 12);
          }
        }
        if (!c.isEmpty()) {
          output.add(new Rhythm12(c, o[sz_tmp - 1], j));

        }
      }
      if (sz_tmp != 0) {
        o[sz_tmp - 1]++;
      }
    }

    output.add(new Rhythm12(new TreeSet<Integer>(), 0, 0));

    return output;
  }

  public static Rhythm12 and(Rhythm12 a, Rhythm12 b) {
    BitSet x = new BitSet(12);
    x.or(a);
    x.and(b);
    return identifyRhythm12(new Combination(x, 12));
  }

  public static Rhythm12 or(Rhythm12 a, Rhythm12 b) {
    BitSet x = new BitSet(12);
    x.or(a);
    x.or(b);
    return identifyRhythm12(new Combination(x, 12));
  }

  public static Rhythm12 not(Rhythm12 a) {
    BitSet x = new BitSet(12);
    x.set(0, 12);
    x.andNot(a);
    return identifyRhythm12(new Combination(x, 12));
  }

  public static Rhythm12 xor(Rhythm12 a, Rhythm12 b) {
    BitSet x = new BitSet(12);
    x.or(a);
    x.xor(b);
    return identifyRhythm12(new Combination(x, 12));

  }

  public static Rhythm12 minus(Rhythm12 a, Rhythm12 b) {
    BitSet x = new BitSet(12);
    x.or(a);
    x.and(not(b));
    return identifyRhythm12(new Combination(x, 12));

  }
  
  private static void generateMaps() {
    TreeSet<Rhythm12> t = Generate();

    TreeMap<String, Rhythm12> outputByOctal = new TreeMap<String, Rhythm12>();
    TreeMap<String, Rhythm12> outputById = new TreeMap<String, Rhythm12>();
    Iterator<Rhythm12> i = t.iterator();

    while (i.hasNext()) {
      Rhythm12 tmp = i.next();
      outputByOctal.put(tmp.toString(), tmp);
      outputById.put(tmp.getId(), tmp);
    }
    RhythmDictByOctal = outputByOctal;
    RhythmDictById = outputById;
  }

  public static LinkedList<Rhythm12> parseRhythm12Seq(String input) {
    String[] s = input.split("\\s");
    LinkedList<Rhythm12> output = new LinkedList<Rhythm12>();
    for (int i = 0; i < s.length; i++) {
      output.add(parseRhythm12Octal(s[i]));
    }
    return output;
  }

  public static Rhythm12 identifyRhythm12(Set<Integer> input) {
    return RhythmDictByOctal.get(Rhythm12.toString(new Combination(12, input)));
  }

  public static Rhythm12 identifyRhythm12(Combination input) {
    return RhythmDictByOctal.get(Rhythm12.toString(input));
  }

  public static Rhythm12 tapEcho(Rhythm12 r, int nbTaps, int tapLen) {
    BitSet x = new BitSet(12);
    x.or(r);
    for(int i=0;i<nbTaps;i++){
      for(int j=0;j<12;j++) {
        if(x.get(j)) {
          x.set((j+tapLen*(i+1))%12);
        }
      }
    }
    return identifyRhythm12(new Combination(x, 12));
  }
  public static Sequence calcSpectrum24(Rhythm12 a, Rhythm12 b) {
    Integer[] x = new Integer[24];

    for (int i = 0; i < 12; i++) {
      x[i] = a.get(i) ? 1 : 0;
      x[i + 12] = b.get(i) ? 1 : 0;
    }
    TreeMap<Integer, Sequence> s = calcIntervalVector(x);
    if (!s.containsKey(1)) {
      Sequence q = new Sequence();
      for (int i = 0; i < 12; i++) {
        q.add(0);
      }

      return q;
    } else {
      return s.get(1);
    }

  }
}
