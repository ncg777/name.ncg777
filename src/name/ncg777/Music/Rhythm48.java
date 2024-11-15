package name.ncg777.Music;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import name.ncg777.CS.DataStructures.CollectionUtils;
import name.ncg777.Maths.Enumerations.MixedRadixEnumeration;
import name.ncg777.Maths.Objects.Combination;
import name.ncg777.Statistics.RandomNumberGenerator;

import static com.google.common.math.IntMath.checkedPow;

/**
 * The {@code Rhythm48} class represents a rhythmic structure based on a 48-element pattern,
 * extending the {@code Rhythm} class. This class provides methods specifically designed for
 * manipulating and analyzing rhythmic sequences of this size, including operations for
 * rotation, parsing, and various bitwise manipulations.
 * 
 * <p>This class is particularly useful in musical compositions and rhythm generation,
 * allowing for the creation and randomization of complex rhythmic patterns.</p>
 *
 * <p>Key functionalities include:</p>
 * <ul>
 * <li>{@code rotate(Rhythm48 r, int t)}: Returns a new {@code Rhythm48} by rotating
 * the rhythm by a specified number of steps.</li>
 * <li>{@code parseRhythm48Tribbles(String input)}: Parses a string representation of a
 * rhythm in "tribble" format (hexadecimal) and returns the corresponding {@code Rhythm48}.</li>
 * <li>{@code getZeroRhythm()}: Returns a zero rhythm, where all elements are inactive.</li>
 * <li>{@code getValid4Beats()} and {@code getValid3Beats()}: Generate and return collections
 * of valid 3-beat and 4-beat rhythmic patterns, respectively.</li>
 * <li>{@code Generate()}: Produces all unique {@code Rhythm48} combinations based on valid beats.</li>
 * <li>Bitwise operations such as {@code and(Rhythm48 a, Rhythm48 b)}, 
 * {@code or(Rhythm48 a, Rhythm48 b)}, {@code not(Rhythm48 a)}, 
 * {@code xor(Rhythm48 a, Rhythm48 b)}, and {@code minus(Rhythm48 a, Rhythm48 b)}.</li>
 * <li>{@code getRandomRhythm48(Predicate<Rhythm> pred)}: Generates a random {@code Rhythm48} 
 * that satisfies a given predicate condition.</li>
 * <li>{@code randomizeBeat(int n, Predicate<Rhythm> pred)}: Randomizes a specified number 
 * of beats within the rhythm based on a given predicate.</li>
 * </ul>
 *
 * <p>Example Usage:</p>
 * <pre>
 * Rhythm48 rhythm = Rhythm48.parseRhythm48Tribbles("1F0F0F0F");
 * Rhythm48 rotatedRhythm = Rhythm48.rotate(rhythm, 4);
 * Rhythm48 zeroRhythm = Rhythm48.getZeroRhythm();
 * </pre>
 *
 * @see Rhythm
 * @see Combination
 */
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
    return new Rhythm48(new TreeSet<Integer>());
  }
  
  public static TreeSet<Rhythm> getValid4Beats() {
    if(valid4beats != null) return valid4beats;
    TreeSet<Rhythm> output = new TreeSet<>();

    // 16th T
    Rhythm[] T4th = new Rhythm[2];
    BitSet b1 = new BitSet(3);
    T4th[0] = new Rhythm(b1, 3);
    b1.set(0);
    T4th[1] = new Rhythm(b1, 3);
    
    {
      List<Integer> base = new ArrayList<Integer>();
      for (int i = 0; i < 4; i++)
        base.add(2);
      MixedRadixEnumeration mre = new MixedRadixEnumeration(base);

      while (mre.hasMoreElements()) {
        var e = mre.nextElement();
        output.add(T4th[e[0]].juxtapose(T4th[e[1]]).juxtapose(T4th[e[2]]).juxtapose(T4th[e[3]]));
      }
    }
    valid4beats = output;
    return output;
  }
  
  private static TreeSet<Rhythm> valid4beats = Rhythm48.getValid4Beats();
  
  public static TreeSet<Rhythm> getValid3Beats() {
    if(valid3beats != null) return valid3beats;
    TreeSet<Rhythm> output = new TreeSet<>();

    Rhythm[] T3rd = new Rhythm[2];
    BitSet b2 = new BitSet(4);
    T3rd[0] = new Rhythm(b2, 4);
    b2.set(0);
    T3rd[1] = new Rhythm(b2, 4);
    
    {
      List<Integer> base = new ArrayList<Integer>();
      for (int i = 0; i < 3; i++)
        base.add(2);
      MixedRadixEnumeration mre = new MixedRadixEnumeration(base);

      while (mre.hasMoreElements()) {
        var e = mre.nextElement();
        output.add(T3rd[e[0]].juxtapose(T3rd[e[1]]).juxtapose(T3rd[e[2]]));
      }
    }
    valid3beats = output;
    
    return output;
  }
  private static TreeSet<Rhythm> valid3beats = Rhythm48.getValid3Beats();
  private static TreeSet<Rhythm> validBeats = Rhythm48.getValidBeats();
  
  public static Rhythm48 getRandomRhythm48(Predicate<Rhythm> pred) {
    return getZeroRhythm().randomizeBeat(1, pred).get(0)
        .randomizeBeat(1, pred).get(0)
        .randomizeBeat(1, pred).get(0);
  }
  
  private static TreeSet<Rhythm> getValidBeats() {
    if(validBeats != null) return validBeats;
    TreeSet<Rhythm> output = new TreeSet<>();
    output.addAll(getValid3Beats());
    output.addAll(getValid4Beats());
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
    //Long szl = Long.valueOf(sz);

    //szl = szl * szl * szl * szl;
    //Long counter = 0l;
    MixedRadixEnumeration mre = new MixedRadixEnumeration(base);
    while (mre.hasMoreElements()) {
      var e = mre.nextElement();
      output.add(Rhythm48.fromRhythm(
          beats[e[0]].juxtapose(beats[e[1]]).juxtapose(beats[e[2]]).juxtapose(beats[e[3]])));
      //System.out.println(Long.toString(++counter) + "/" + Long.toString(szl));
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
  
  public List<Rhythm48> randomizeBeat(int n, Predicate<Rhythm> pred) {
    ArrayList<Rhythm48> output = new ArrayList<Rhythm48>();
    
    for(int i=0;i<n;i++) {
      while(true) {
        int nbb = RandomNumberGenerator.nextInt(3)+1;
        BitSet bs = new BitSet(48);
        bs.or(this);
        while(nbb-- > 0) {
          int b = RandomNumberGenerator.nextInt(4);
          
          Rhythm newBeat = null;
          
          if(RandomNumberGenerator.nextDouble() > 0.5) {
            newBeat = CollectionUtils.chooseAtRandom(valid3beats);
          } else {
            newBeat = CollectionUtils.chooseAtRandom(valid4beats);
          }
          
          for(int j=0;j<12;j++) {
            bs.set((b*12)+j, newBeat.get(j));
          }
        }
        Rhythm r = Rhythm.buildRhythm(bs, 48);
        
        if(pred.test(r)) {
          output.add(Rhythm48.fromRhythm(r));
          break;
        }
      }
    }
    return output;
  }
  
}
