package name.ncg777.maths.objects.words;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Set;

import name.ncg777.maths.objects.Combination;

import static com.google.common.math.IntMath.checkedPow;
//import static name.ncg777.maths.objects.Sequence.ReverseComparator;

public class OctalWord extends Combination implements Serializable{

  private static final long serialVersionUID = 1L;
  String m_str;

  public static OctalWord rotate(OctalWord r, int t) {
    return tryConvert(r.rotate(t));
  }

  public static OctalWord parse(String input) {    
    String binstr = Integer.toBinaryString(Integer.parseInt(input, 8));
    
    BitSet b = new BitSet(binstr.length()*3);
    
    for(int i=0;i<binstr.length();i++) {
      b.set(i, binstr.charAt(i) == '1');
    }
    return new OctalWord(b);
  }

  private OctalWord(Set<Integer> p_s) {
    super(12, p_s);

    m_str = OctalWord.toString(this);
  }
  protected OctalWord(BitSet b) {
    super(b, 12);
  }
  public BinaryWord asRhythm() {
    BitSet b = new BitSet(12);
    b.or(this);
    return new BinaryWord(b, 12);
  }

  public static OctalWord tryConvert(BinaryWord r) {
    if(r.getN()!=12) throw new RuntimeException();
    Combination c = new Combination(12);
    c.or(r);
    return tryConvert(c);
  }
  
  @Override
  public String toString() {
    if (m_str == null) {
      m_str = OctalWord.toString(this);
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

//  public static OctalWord getZeroRhythm(){
//    return parseOctalWord("00 00");
//  }

//  public static TreeSet<OctalWord> Generate() {
//    Integer[] o = new Integer[12];
//
//    for (int i = 0; i < 12; i++) {
//      o[i] = 0;
//    }
//
//    TreeSet<OctalWord> output = new TreeSet<OctalWord>();
//    TreeSet<Necklace> nck = Necklace.generate(12, 2);
//    Necklace[] arr = new Necklace[nck.size()];
//
//    int z = 0;
//    Iterator<Necklace> i = nck.iterator();
//    while (i.hasNext()) {
//      arr[z] = i.next();
//      z++;
//    }
//
//    Arrays.sort(arr, new ReverseComparator());
//
//    for (int l = 0; l < z; l++) {
//      Necklace n = arr[l];
//
//
//      int sz_tmp = 0;
//      for (int k = 0; k < 12; k++) {
//        if (n.get(k).equals(1)) {
//          sz_tmp++;
//        }
//      }
//
//      for (int j = 0; j < 12; j++) {
//        TreeSet<Integer> c = new TreeSet<Integer>();
//        for (int k = 0; k < 12; k++) {
//          if (n.get(k).equals(1)) {
//            c.add(((12 - (k + 1)) + j) % 12);
//          }
//        }
//        if (!c.isEmpty()) {
//          output.add(new OctalWord(c));
//
//        }
//      }
//      if (sz_tmp != 0) {
//        o[sz_tmp - 1]++;
//      }
//    }
//
//    output.add(new OctalWord(new TreeSet<Integer>()));
//
//    return output;
//  }

  public static OctalWord and(OctalWord a, OctalWord b) {
    BitSet x = new BitSet(12);
    x.or(a);
    x.and(b);
    return tryConvert(new Combination(x, 12));
  }

  public static OctalWord or(OctalWord a, OctalWord b) {
    BitSet x = new BitSet(12);
    x.or(a);
    x.or(b);
    return tryConvert(new Combination(x, 12));
  }

  public static OctalWord not(OctalWord a) {
    BitSet x = new BitSet(12);
    x.set(0, 12);
    x.andNot(a);
    return tryConvert(new Combination(x, 12));
  }

  public static OctalWord xor(OctalWord a, OctalWord b) {
    BitSet x = new BitSet(12);
    x.or(a);
    x.xor(b);
    return tryConvert(new Combination(x, 12));

  }

  public static OctalWord minus(OctalWord a, OctalWord b) {
    BitSet x = new BitSet(12);
    x.or(a);
    x.and(not(b));
    return tryConvert(new Combination(x, 12));

  }

  public static OctalWord tryConvert(Set<Integer> input) {
    return new OctalWord(input);
  }

  public static OctalWord tryConvert(Combination input) {
    return new OctalWord(input);
  }
}
