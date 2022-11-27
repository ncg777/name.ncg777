package name.NicolasCoutureGrenier.Music;

import java.io.Serializable;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import name.NicolasCoutureGrenier.Maths.DataStructures.Combination;
import name.NicolasCoutureGrenier.Maths.DataStructures.Necklace;

import static com.google.common.math.IntMath.checkedPow;
import static name.NicolasCoutureGrenier.Maths.DataStructures.Sequence.ReverseComparator;

public class Rhythm16 extends Rhythm implements Serializable{

  private static final long serialVersionUID = 1L;

  static TreeMap<String, Rhythm16> RhythmDictByHex;

  String m_str;

  static {
    generateMaps();
  }

  public static Rhythm16 rotate(Rhythm16 r, int t) {
    return identifyRhythm16(Rhythm.rotate(r, t));
  }

  public static TreeMap<String, Rhythm16> getRhythm16Dict() {
    return RhythmDictByHex;
  }

  public static TreeSet<Rhythm16> getRhythms16() {
    TreeSet<Rhythm16> output = new TreeSet<Rhythm16>();
    output.addAll(RhythmDictByHex.values());
    return output;
  }

  public static Rhythm16 parseRhythm16Hex(String input) {
    return RhythmDictByHex.get(input);
  }
  
  private Rhythm16(Set<Integer> p_s) {
    super(16, p_s);

    m_str = Rhythm16.toString(this);

  }

  public Rhythm asRhythm() {
    BitSet b = new BitSet(16);
    b.or(this);
    return new Rhythm(b, 16);
  }

  public static Rhythm16 fromRhythm(Rhythm r) {
    if(r.getN()!=16) throw new RuntimeException();
    Combination c = new Combination(16);
    c.or(r);
    return identifyRhythm16(c);
  }
  
  @Override
  public String toString() {
    if (m_str == null) {
      m_str = Rhythm16.toString(this);
    }
    return m_str;
  }
  
  public static String toString(Combination c) {
    int msb = 0;
    int lsb = 0;

    for (int i = c.nextSetBit(0); i >= 0; i = c.nextSetBit(i + 1)) {
      if (i >= 8) {
        lsb += checkedPow(2, 7 - (i - 8));
      } else {
        msb += checkedPow(2, 7 - i);
      }
    }
    return String.format("%02X %02X", msb, lsb);
  }


  public static Rhythm16 getZeroRhythm(){
    return parseRhythm16Hex("00 00");
  }

  static TreeSet<Rhythm16> Generate() {
    Integer[] o = new Integer[16];

    for (int i = 0; i < 16; i++) {
      o[i] = 0;
    }

    TreeSet<Rhythm16> output = new TreeSet<Rhythm16>();
    TreeSet<Necklace> nck = Necklace.generate(16, 2);
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
      for (int k = 0; k < 16; k++) {
        if (n.get(k).equals(1)) {
          sz_tmp++;
        }
      }

      for (int j = 0; j < 16; j++) {
        TreeSet<Integer> c = new TreeSet<Integer>();
        for (int k = 0; k < 16; k++) {
          if (n.get(k).equals(1)) {
            c.add(((16 - (k + 1)) + j) % 16);
          }
        }
        if (!c.isEmpty()) {
          output.add(new Rhythm16(c));

        }
      }
      if (sz_tmp != 0) {
        o[sz_tmp - 1]++;
      }
    }

    output.add(new Rhythm16(new TreeSet<Integer>()));

    return output;
  }

  public static Rhythm16 and(Rhythm16 a, Rhythm16 b) {
    BitSet x = new BitSet(16);
    x.or(a);
    x.and(b);
    return identifyRhythm16(new Combination(x, 16));
  }

  public static Rhythm16 or(Rhythm16 a, Rhythm16 b) {
    BitSet x = new BitSet(16);
    x.or(a);
    x.or(b);
    return identifyRhythm16(new Combination(x, 16));
  }

  public static Rhythm16 not(Rhythm16 a) {
    BitSet x = new BitSet(16);
    x.set(0, 16);
    x.andNot(a);
    return identifyRhythm16(new Combination(x, 16));
  }

  public static Rhythm16 xor(Rhythm16 a, Rhythm16 b) {
    BitSet x = new BitSet(16);
    x.or(a);
    x.xor(b);
    return identifyRhythm16(new Combination(x, 16));

  }

  public static Rhythm16 minus(Rhythm16 a, Rhythm16 b) {
    BitSet x = new BitSet(16);
    x.or(a);
    x.and(not(b));
    return identifyRhythm16(new Combination(x, 16));

  }
  
  private static void generateMaps() {
    TreeSet<Rhythm16> t = Generate();

    TreeMap<String, Rhythm16> outputByHex = new TreeMap<String, Rhythm16>();
    Iterator<Rhythm16> i = t.iterator();

    while (i.hasNext()) {
      Rhythm16 tmp = i.next();
      outputByHex.put(tmp.toString(), tmp);
    }
    RhythmDictByHex = outputByHex;
  }

  public static LinkedList<Rhythm16> parseRhythm16Seq(String input) {
    String[] s = input.split("\\s");
    LinkedList<Rhythm16> output = new LinkedList<Rhythm16>();
    for (int i = 0; i < s.length; i++) {
      output.add(parseRhythm16Hex(s[i]));
    }
    return output;
  }

  public static Rhythm16 identifyRhythm16(Set<Integer> input) {
    return RhythmDictByHex.get(Rhythm16.toString(new Combination(16, input)));
  }

  public static Rhythm16 identifyRhythm16(Combination input) {
    return RhythmDictByHex.get(Rhythm16.toString(input));
  }
}
