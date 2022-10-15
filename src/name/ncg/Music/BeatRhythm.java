package name.ncg.Music;

import static com.google.common.math.IntMath.checkedPow;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.TreeMap;

import name.ncg.Maths.Combination;
import name.ncg.Maths.Necklace;
import name.ncg.Maths.DataStructures.Sequence.ReverseComparator;

public class BeatRhythm extends Rhythm {
  private static final long serialVersionUID = -5152258002409941959L;
  final public static int Clicks = 96; // must be divisible by 8
  final private int bitlen;
  final private static int MAX_N = 4;
  private static TreeSet<BeatRhythm> beatRhythms = new TreeSet<>();
  private static TreeMap<String, BeatRhythm> beatRhythmDict = new TreeMap<>();
  
  @SuppressWarnings("unchecked")
  public static TreeSet<BeatRhythm> getBeatRhythms() {
    return (TreeSet<BeatRhythm>)beatRhythms.clone();
  }
  
  public BitSet getBitSetClicks() {
    BitSet bitsetClicks = new BitSet(96);
    for (int i = nextSetBit(0); i >= 0; i = nextSetBit(i + 1)) {
      bitsetClicks.set(i*bitlen, true);
    }
    return bitsetClicks;
  }
  
  public Rhythm getRhythmClicks() {
    return Rhythm.buildRhythm(getBitSetClicks(), Clicks);
  }
  
  
  private static String toHexString(BitSet b) {
    StringBuilder sb = new StringBuilder();
    
    for(int i=0; i<Clicks/8; i++) {
      BitSet byt = new BitSet(8);
      for(int j=0; j<8;j++) {
        byt.set(j, b.get((i*8) + j));
      }
      sb.append(ByteToString(byt) + " ");
    }
    return sb.toString().trim();
  }
  
  @Override
  public String toString() {
    return toHexString(getBitSetClicks());
  }
  
  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    
    return toString().equals(((BeatRhythm)obj).toString());
  }
  
  static {
    for(int i=1;i<=MAX_N;i++) {
      if(Clicks%i == 0) { beatRhythms.addAll(Generate(i)); } 
    }
    
    for(var b : beatRhythms) { beatRhythmDict.put(b.toString(), b);}
  }
  
  public static BeatRhythm identifyBeatRhythm(Set<Integer> input) {
    return BeatRhythm.parseBeatRhythm(toHexString(new Combination(Clicks, input)));
  }
  
  public static BeatRhythm parseBeatRhythm(String s) {
    if(!beatRhythmDict.containsKey(s.trim())) throw new RuntimeException("not found");
    return beatRhythmDict.get(s.trim()); 
  }
  public static BeatRhythm fromRhythmClicks(Rhythm r) {
    return parseBeatRhythm(toHexString(r));
  }
  
  static TreeSet<BeatRhythm> Generate(int n) {
    TreeSet<BeatRhythm> output = new TreeSet<BeatRhythm>();
    TreeSet<Necklace> nck = Necklace.generate(n, 2);
    Necklace[] arr = new Necklace[nck.size()];

    int z = 0;
    Iterator<Necklace> i = nck.iterator();
    while (i.hasNext()) {
      arr[z] = i.next();
      z++;
    }

    Arrays.sort(arr, new ReverseComparator());

    for (int l = 0; l < z; l++) {
      Necklace necklace = arr[l];

      for (int j = 0; j < n; j++) {
        TreeSet<Integer> c = new TreeSet<Integer>();
        for (int k = 0; k < n; k++) {
          if (necklace.get(k).equals(1)) {
            c.add(((n - (k + 1)) + j) % n);
          }
        }
        if (!c.isEmpty()) {
          output.add(new BeatRhythm(n, c));
        }
      }
    }

    output.add(new BeatRhythm(n, new TreeSet<Integer>()));

    return output;
  }
  
  private static String ByteToString(BitSet b) {
    int intbyte = 0;
    
    for (int i = b.nextSetBit(0); i >= 0; i = b.nextSetBit(i + 1)) {
        intbyte += checkedPow(2, 7 - i);  
    }
    return String.format("%02X", intbyte);
  }
  
  
  public BeatRhythm(BitSet b, int n) {
    super(b, n);
    if(Clicks%n != 0) throw new IllegalArgumentException("Unsupported division");
    bitlen = Clicks / getN();
  }

  public BeatRhythm(Integer n, Set<Integer> s) {
    super(n, s);
    if(Clicks%n != 0) throw new IllegalArgumentException("Unsupported division");
    bitlen = Clicks / getN();
  }

}
