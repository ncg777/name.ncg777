package name.ncg777.maths.objects.words;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Set;

import name.ncg777.maths.objects.Combination;

import static com.google.common.math.IntMath.checkedPow;

public class HexadecimalWord extends Combination implements Serializable{
  private static final long serialVersionUID = 1L;
  String m_str;

  public static HexadecimalWord rotate(HexadecimalWord r, int t) {
    return tryConvert(r.rotate(t));
  }

  public static HexadecimalWord parse(String input) {
    String binstr = Integer.toBinaryString(Integer.parseInt(input, 16));

    BitSet b = new BitSet(binstr.length()*4);
    
    for(int i=0;i<16;i++) {
      b.set(i, binstr.charAt(i) == '1');
    }
    return new HexadecimalWord(b);
  }
  
  private HexadecimalWord(Set<Integer> p_s) {
    super(16, p_s);

    m_str = HexadecimalWord.toString(this);

  }
  protected HexadecimalWord(BitSet b) {
    super(b, 16);
  }
  public BinaryWord asRhythm() {
    BitSet b = new BitSet(16);
    b.or(this);
    return new BinaryWord(b, 16);
  }

  public static HexadecimalWord fromBinary(BinaryWord r) {
    if(r.getN()!=16) throw new RuntimeException();
    Combination c = new Combination(16);
    c.or(r);
    return tryConvert(c);
  }
  
  @Override
  public String toString() {
    if (m_str == null) {
      m_str = HexadecimalWord.toString(this);
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

  public static HexadecimalWord getZeroRhythm(){
    return parse("00 00");
  }

  public static HexadecimalWord and(HexadecimalWord a, HexadecimalWord b) {
    BitSet x = new BitSet(16);
    x.or(a);
    x.and(b);
    return tryConvert(new Combination(x, 16));
  }

  public static HexadecimalWord or(HexadecimalWord a, HexadecimalWord b) {
    BitSet x = new BitSet(16);
    x.or(a);
    x.or(b);
    return tryConvert(new Combination(x, 16));
  }

  public static HexadecimalWord not(HexadecimalWord a) {
    BitSet x = new BitSet(16);
    x.set(0, 16);
    x.andNot(a);
    return tryConvert(new Combination(x, 16));
  }

  public static HexadecimalWord xor(HexadecimalWord a, HexadecimalWord b) {
    BitSet x = new BitSet(16);
    x.or(a);
    x.xor(b);
    return tryConvert(new Combination(x, 16));

  }

  public static HexadecimalWord minus(HexadecimalWord a, HexadecimalWord b) {
    BitSet x = new BitSet(16);
    x.or(a);
    x.and(not(b));
    return tryConvert(new Combination(x, 16));

  }

  public static HexadecimalWord tryConvert(Set<Integer> input) {
    return new HexadecimalWord(input);
  }

  public static HexadecimalWord tryConvert(Combination input) {
    return new HexadecimalWord(input);
  }
}
