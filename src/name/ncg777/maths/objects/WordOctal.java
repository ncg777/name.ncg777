package name.ncg777.maths.objects;

import java.io.Serializable;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import static com.google.common.math.IntMath.checkedPow;
import static name.ncg777.maths.objects.Sequence.ReverseComparator;

public class WordOctal extends WordBinary implements Serializable{

  private static final long serialVersionUID = 1L;
  
  String m_str;

  public static WordOctal rotate(WordOctal r, int t) {
    return identifyRhythm12(WordBinary.rotate(r, t));
  }

  public static WordOctal parseOctalWord(String input) {    
    String binstr = Integer.toBinaryString(Integer.parseInt(input, 8));
    
    BitSet b = new BitSet(binstr.length()*3);
    
    for(int i=0;i<binstr.length();i++) {
      b.set(i, binstr.charAt(i) == '1');
    }
    return new WordOctal(b);
  }

  private WordOctal(Set<Integer> p_s) {
    super(12, p_s);

    m_str = WordOctal.toString(this);
  }
  protected WordOctal(BitSet b) {
    super(b, 12);
  }
  public WordBinary asRhythm() {
    BitSet b = new BitSet(12);
    b.or(this);
    return new WordBinary(b, 12);
  }

  public static WordOctal fromRhythm(WordBinary r) {
    if(r.getN()!=12) throw new RuntimeException();
    Combination c = new Combination(12);
    c.or(r);
    return identifyRhythm12(c);
  }
  
  @Override
  public String toString() {
    if (m_str == null) {
      m_str = WordOctal.toString(this);
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

  public static WordOctal getZeroRhythm(){
    return parseOctalWord("00 00");
  }

  public static TreeSet<WordOctal> Generate() {
    Integer[] o = new Integer[12];

    for (int i = 0; i < 12; i++) {
      o[i] = 0;
    }

    TreeSet<WordOctal> output = new TreeSet<WordOctal>();
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
          output.add(new WordOctal(c));

        }
      }
      if (sz_tmp != 0) {
        o[sz_tmp - 1]++;
      }
    }

    output.add(new WordOctal(new TreeSet<Integer>()));

    return output;
  }

  public static WordOctal and(WordOctal a, WordOctal b) {
    BitSet x = new BitSet(12);
    x.or(a);
    x.and(b);
    return identifyRhythm12(new Combination(x, 12));
  }

  public static WordOctal or(WordOctal a, WordOctal b) {
    BitSet x = new BitSet(12);
    x.or(a);
    x.or(b);
    return identifyRhythm12(new Combination(x, 12));
  }

  public static WordOctal not(WordOctal a) {
    BitSet x = new BitSet(12);
    x.set(0, 12);
    x.andNot(a);
    return identifyRhythm12(new Combination(x, 12));
  }

  public static WordOctal xor(WordOctal a, WordOctal b) {
    BitSet x = new BitSet(12);
    x.or(a);
    x.xor(b);
    return identifyRhythm12(new Combination(x, 12));

  }

  public static WordOctal minus(WordOctal a, WordOctal b) {
    BitSet x = new BitSet(12);
    x.or(a);
    x.and(not(b));
    return identifyRhythm12(new Combination(x, 12));

  }

  public static WordOctal identifyRhythm12(Set<Integer> input) {
    return new WordOctal(input);
  }

  public static WordOctal identifyRhythm12(Combination input) {
    return new WordOctal(input);
  }
}
