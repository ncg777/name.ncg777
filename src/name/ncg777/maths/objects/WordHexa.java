package name.ncg777.maths.objects;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Set;

import static com.google.common.math.IntMath.checkedPow;

public class WordHexa extends WordBinary implements Serializable{

  private static final long serialVersionUID = 1L;

  String m_str;

  public static WordHexa rotate(WordHexa r, int t) {
    return identifyRhythmHexa(WordBinary.rotate(r, t));
  }

  public static WordHexa parseRhythmHexa(String input) {
    String binstr = Integer.toBinaryString(Integer.parseInt(input, 16));

    BitSet b = new BitSet(binstr.length()*4);
    
    for(int i=0;i<16;i++) {
      b.set(i, binstr.charAt(i) == '1');
    }
    return new WordHexa(b);
  }
  
  private WordHexa(Set<Integer> p_s) {
    super(16, p_s);

    m_str = WordHexa.toString(this);

  }
  protected WordHexa(BitSet b) {
    super(b, 16);
  }
  public WordBinary asRhythm() {
    BitSet b = new BitSet(16);
    b.or(this);
    return new WordBinary(b, 16);
  }

  public static WordHexa fromRhythm(WordBinary r) {
    if(r.getN()!=16) throw new RuntimeException();
    Combination c = new Combination(16);
    c.or(r);
    return identifyRhythmHexa(c);
  }
  
  @Override
  public String toString() {
    if (m_str == null) {
      m_str = WordHexa.toString(this);
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

  public static WordHexa getZeroRhythm(){
    return parseRhythmHexa("00 00");
  }

  public static WordHexa and(WordHexa a, WordHexa b) {
    BitSet x = new BitSet(16);
    x.or(a);
    x.and(b);
    return identifyRhythmHexa(new Combination(x, 16));
  }

  public static WordHexa or(WordHexa a, WordHexa b) {
    BitSet x = new BitSet(16);
    x.or(a);
    x.or(b);
    return identifyRhythmHexa(new Combination(x, 16));
  }

  public static WordHexa not(WordHexa a) {
    BitSet x = new BitSet(16);
    x.set(0, 16);
    x.andNot(a);
    return identifyRhythmHexa(new Combination(x, 16));
  }

  public static WordHexa xor(WordHexa a, WordHexa b) {
    BitSet x = new BitSet(16);
    x.or(a);
    x.xor(b);
    return identifyRhythmHexa(new Combination(x, 16));

  }

  public static WordHexa minus(WordHexa a, WordHexa b) {
    BitSet x = new BitSet(16);
    x.or(a);
    x.and(not(b));
    return identifyRhythmHexa(new Combination(x, 16));

  }

  public static WordHexa identifyRhythm16(Set<Integer> input) {
    return new WordHexa(input);
  }

  public static WordHexa identifyRhythmHexa(Combination input) {
    return new WordHexa(input);
  }
}
