package name.ncg777.musical;

import java.io.Serializable;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import name.ncg777.mathematics.objects.Combination;
import name.ncg777.mathematics.objects.Necklace;

import static com.google.common.math.IntMath.checkedPow;
import static name.ncg777.mathematics.objects.Sequence.ReverseComparator;

/**
 * The {@code Rhythm12} class represents a specific type of rhythm based on a 12-element 
 * rhythmic structure. It extends the {@code Rhythm} class and provides methods for 
 * various operations specific to a 12-member rhythmic pattern, such as rotation, bitwise 
 * operations (AND, OR, NOT, XOR), and parsing from octal representations.
 *
 * <p>This class is especially useful in applications involving musical rhythms, 
 * enabling analysis, manipulation, and generation of rhythmic sequences.</p>
 *
 * <p>Key functionalities include:</p>
 * <ul>
 * <li>{@code rotate(Rhythm12 r, int t)}: Returns a new {@code Rhythm12} by rotating 
 * the rhythm by a specified number of steps.</li>
 * <li>{@code parseRhythm12Octal(String input)}: Parses a string representing a rhythm 
 * in octal format and returns the corresponding {@code Rhythm12} representation.</li>
 * <li>{@code getZeroRhythm()}: Returns a zero rhythm, equivalent to all elements being 
 * inactive.</li>
 * <li>{@code Generate()}: Generates all unique {@code Rhythm12} patterns based on 
 * combinatorial properties.</li>
 * <li>Standard bitwise operations like {@code and(Rhythm12 a, Rhythm12 b)}, 
 * {@code or(Rhythm12 a, Rhythm12 b)}, {@code not(Rhythm12 a)}, 
 * {@code xor(Rhythm12 a, Rhythm12 b)}, and {@code minus(Rhythm12 a, Rhythm12 b)}.</li>
 * </ul>
 *
 * <p>Example Usage:</p>
 * <pre>
 * Rhythm12 rhythm = Rhythm12.parseRhythm12Octal("01 10");
 * Rhythm12 newRhythm = Rhythm12.rotate(rhythm, 3);
 * Rhythm12 zeroRhythm = Rhythm12.getZeroRhythm();
 * </pre>
 *
 * @see Rhythm
 * @see Combination
 */
public class Rhythm12 extends Rhythm implements Serializable{

  private static final long serialVersionUID = 1L;
  
  String m_str;

  public static Rhythm12 rotate(Rhythm12 r, int t) {
    return identifyRhythm12(Rhythm.rotate(r, t));
  }

  public static Rhythm12 parseRhythm12Octal(String input) {    
    String binstr = Integer.toBinaryString(Integer.parseInt(input.replaceAll("\\s+", ""), 8));
    StringBuilder sb = new StringBuilder(binstr);
    int k = 12-binstr.length();
    while(k-- > 0) {sb.insert(0, '0');}
    binstr = sb.toString();
    BitSet b = new BitSet(12);
    
    for(int i=0;i<12;i++) {
      b.set(i, binstr.charAt(i) == '1');
    }
    return new Rhythm12(b);
  }

  private Rhythm12(Set<Integer> p_s) {
    super(12, p_s);

    m_str = Rhythm12.toString(this);
  }
  protected Rhythm12(BitSet b) {
    super(b, 12);
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

  public static Rhythm12 getZeroRhythm(){
    return parseRhythm12Octal("00 00");
  }

  public static TreeSet<Rhythm12> Generate() {
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
          output.add(new Rhythm12(c));

        }
      }
      if (sz_tmp != 0) {
        o[sz_tmp - 1]++;
      }
    }

    output.add(new Rhythm12(new TreeSet<Integer>()));

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

  public static Rhythm12 identifyRhythm12(Set<Integer> input) {
    return new Rhythm12(input);
  }

  public static Rhythm12 identifyRhythm12(Combination input) {
    return new Rhythm12(input);
  }
}
