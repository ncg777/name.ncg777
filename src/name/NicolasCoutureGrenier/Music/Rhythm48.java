package name.NicolasCoutureGrenier.Music;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import name.NicolasCoutureGrenier.Maths.DataStructures.Combination;
import name.NicolasCoutureGrenier.Maths.Enumerations.MixedRadixEnumeration;

import static com.google.common.math.IntMath.checkedPow;

public class Rhythm48 extends Rhythm implements Serializable {

  private static final long serialVersionUID = 1L;

  String m_str;

  public static Rhythm48 rotate(Rhythm48 r, int t) {
    return identifyRhythm48(Rhythm.rotate(r, t));
  }

  public static Rhythm48 parseRhythm48Tribbles(String input) {
    input = input.replaceAll("\\s+", "");
    Rhythm r = StringToTribble(input.substring(0, 3))
        .juxtapose(StringToTribble(input.substring(3, 6)))
        .juxtapose(StringToTribble(input.substring(6, 9)))
        .juxtapose(StringToTribble(input.substring(9, 12)));
    return Rhythm48.fromRhythm(r);
  }

  private Rhythm48(Set<Integer> p_s) {
    super(48, p_s);

    m_str = Rhythm48.toString(this);

  }

  protected Rhythm48(BitSet b) {
    super(b, 48);
  }

  public Rhythm asRhythm() {
    BitSet b = new BitSet(48);
    b.or(this);
    return new Rhythm(b, 48);
  }

  public static Rhythm48 fromRhythm(Rhythm r) {
    if (r.getN() != 48) throw new RuntimeException();
    Combination c = new Combination(48);
    c.or(r);
    return identifyRhythm48(c);
  }

  @Override
  public String toString() {
    if (m_str == null) {
      m_str = Rhythm48.toString(this);
    }
    return m_str;
  }

  private static String NibbleToString(BitSet b) {
    int intbyte = 0;

    for (int i = b.nextSetBit(0); i >= 0; i = b.nextSetBit(i + 1)) {
      intbyte += checkedPow(2, 3 - i);
    }
    return String.format("%X", intbyte);
  }
  private static Rhythm StringToTribble(String str) {

    String binstr = Integer.toBinaryString(Integer.parseInt(str, 16));
    StringBuilder sb = new StringBuilder(binstr);
    int k = 12 - binstr.length();
    while (k-- > 0) {
      sb.insert(0, '0');
    }
    binstr = sb.toString();
    BitSet b = new BitSet(12);

    for (int i = 0; i < 12; i++) {
      b.set(i, binstr.charAt(i) == '1');
    }
    return new Rhythm(b,12);
  }

  public static String toString(Combination c) {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < 12; i++) {
      BitSet b = new BitSet(4);

      for (int j = 0; j < 4; j++) {
        b.set(j, c.get((i * 4) + j));
      }
      sb.append(NibbleToString(b));
    }

    sb.insert(9, " ");
    sb.insert(6, " ");
    sb.insert(3, " ");
    return sb.toString();
  }


  public static Rhythm48 getZeroRhythm() {
    return parseRhythm48Tribbles("000 000 000 000");
  }
  
  public static TreeSet<Rhythm> getValid4Beats() {
    if(valid4beats != null) return valid4beats;
    TreeSet<Rhythm> output = new TreeSet<>();

    // 16th T
    Rhythm[] T16 = new Rhythm[3];
    BitSet b1 = new BitSet(3);
    T16[0] = new Rhythm(b1, 3);
    b1.set(0);
    T16[1] = new Rhythm(b1, 3);
    b1.set(1);
    b1.set(2);
    T16[2] = new Rhythm(b1, 3);
    {
      List<Integer> base = new ArrayList<Integer>();
      for (int i = 0; i < 4; i++)
        base.add(3);
      MixedRadixEnumeration mre = new MixedRadixEnumeration(base);

      while (mre.hasMoreElements()) {
        var e = mre.nextElement();
        output.add(T16[e[0]].juxtapose(T16[e[1]]).juxtapose(T16[e[2]]).juxtapose(T16[e[3]]));
      }
    }
    valid4beats = output;
    return output;
  }
  
  private static TreeSet<Rhythm> valid4beats = Rhythm48.getValid4Beats();
  
  public static TreeSet<Rhythm> getValid3Beats() {
    if(valid3beats != null) return valid3beats;
    TreeSet<Rhythm> output = new TreeSet<>();

    Rhythm[] T6th = new Rhythm[3];
    BitSet b2 = new BitSet(4);
    T6th[0] = new Rhythm(b2, 4);
    b2.set(0);
    T6th[1] = new Rhythm(b2, 4);
    b2.set(2);
    T6th[2] = new Rhythm(b2, 4);
    {
      List<Integer> base = new ArrayList<Integer>();
      for (int i = 0; i < 3; i++)
        base.add(3);
      MixedRadixEnumeration mre = new MixedRadixEnumeration(base);

      while (mre.hasMoreElements()) {
        var e = mre.nextElement();
        output.add(T6th[e[0]].juxtapose(T6th[e[1]]).juxtapose(T6th[e[2]]));
      }
    }
    valid3beats = output;
    
    return output;
  }
  private static TreeSet<Rhythm> valid3beats = Rhythm48.getValid3Beats();
  private static TreeSet<Rhythm> validBeats = Rhythm48.getValidBeats();
  private static TreeSet<Rhythm> getValidBeats() {
    if(validBeats != null) return validBeats;
    TreeSet<Rhythm> output = new TreeSet<>();

    // 16th T
    Rhythm[] T16 = new Rhythm[2];
    BitSet b1 = new BitSet(3);
    T16[0] = new Rhythm(b1, 3);
    b1.set(0);
    T16[1] = new Rhythm(b1, 3);
    // b1.set(1);
    // b1.set(2);
    // T16[2] = new Rhythm(b1, 3);
    {
      List<Integer> base = new ArrayList<Integer>();
      for (int i = 0; i < 4; i++)
        base.add(2);
      MixedRadixEnumeration mre = new MixedRadixEnumeration(base);

      while (mre.hasMoreElements()) {
        var e = mre.nextElement();
        output.add(T16[e[0]].juxtapose(T16[e[1]]).juxtapose(T16[e[2]]).juxtapose(T16[e[3]]));
      }
    }


    Rhythm[] T6th = new Rhythm[2];
    BitSet b2 = new BitSet(4);
    T6th[0] = new Rhythm(b2, 4);
    b2.set(0);
    T6th[1] = new Rhythm(b2, 4);
    {
      List<Integer> base = new ArrayList<Integer>();
      for (int i = 0; i < 3; i++)
        base.add(2);
      MixedRadixEnumeration mre = new MixedRadixEnumeration(base);

      while (mre.hasMoreElements()) {
        var e = mre.nextElement();
        output.add(T6th[e[0]].juxtapose(T6th[e[1]]).juxtapose(T6th[e[2]]));
      }
    }
    validBeats = output;
    return output;
  }

  public static TreeSet<Rhythm48> Generate() {
    TreeSet<Rhythm48> output = new TreeSet<>();
    Rhythm[] beats = getValidBeats().toArray(new Rhythm[0]);
    int sz = beats.length;
    ArrayList<Integer> base = new ArrayList<Integer>();
    for (int i = 0; i < 4; i++)
      base.add(sz);
    Long szl = Long.valueOf(sz);

    szl = szl * szl * szl * szl;
    Long counter = 0l;
    MixedRadixEnumeration mre = new MixedRadixEnumeration(base);
    while (mre.hasMoreElements()) {
      var e = mre.nextElement();
      output.add(Rhythm48.fromRhythm(
          beats[e[0]].juxtapose(beats[e[1]]).juxtapose(beats[e[2]]).juxtapose(beats[e[3]])));
      System.out.println(Long.toString(++counter) + "/" + Long.toString(szl));
    }
    return output;
  }

  public static Rhythm48 and(Rhythm48 a, Rhythm48 b) {
    BitSet x = new BitSet(48);
    x.or(a);
    x.and(b);
    return identifyRhythm48(new Combination(x, 48));
  }

  public static Rhythm48 or(Rhythm48 a, Rhythm48 b) {
    BitSet x = new BitSet(48);
    x.or(a);
    x.or(b);
    return identifyRhythm48(new Combination(x, 48));
  }

  public static Rhythm48 not(Rhythm48 a) {
    BitSet x = new BitSet(48);
    x.set(0, 48);
    x.andNot(a);
    return identifyRhythm48(new Combination(x, 48));
  }

  public static Rhythm48 xor(Rhythm48 a, Rhythm48 b) {
    BitSet x = new BitSet(48);
    x.or(a);
    x.xor(b);
    return identifyRhythm48(new Combination(x, 48));

  }

  public static Rhythm48 minus(Rhythm48 a, Rhythm48 b) {
    BitSet x = new BitSet(48);
    x.or(a);
    x.and(not(b));
    return identifyRhythm48(new Combination(x, 48));

  }

  public static Rhythm48 identifyRhythm48(Set<Integer> input) {
    return new Rhythm48(input);
  }

  public static Rhythm48 identifyRhythm48(Combination input) {
    return new Rhythm48(input);
  }
}
