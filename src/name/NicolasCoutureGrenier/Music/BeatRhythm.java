package name.NicolasCoutureGrenier.Music;

import static com.google.common.math.IntMath.checkedPow;

import java.io.Serializable;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.TreeMap;

import name.NicolasCoutureGrenier.Maths.DataStructures.Combination;
import name.NicolasCoutureGrenier.Maths.DataStructures.Necklace;
import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence.ReverseComparator;

public class BeatRhythm extends Rhythm implements Serializable {
  private static final long serialVersionUID = -5152258002409941959L;
  final public static int NbBits = 12;
  final private int bitlen;
  final private static int MAX_N = 6;
  private static TreeSet<BeatRhythm> beatRhythms = new TreeSet<>();
  private static TreeMap<String, BeatRhythm> beatRhythmDict = new TreeMap<>();
  
  @SuppressWarnings("unchecked")
  public static TreeSet<BeatRhythm> getBeatRhythms() {
    return (TreeSet<BeatRhythm>)beatRhythms.clone();
  }
  
  public BitSet getGroundBitSet() {
    BitSet groundBitset = new BitSet(NbBits);
    for (int i = nextSetBit(0); i >= 0; i = nextSetBit(i + 1)) {
      groundBitset.set(i*bitlen, true);
    }
    return groundBitset;
  }
  
  public Rhythm getGroundRhythm() {
    return Rhythm.buildRhythm(getGroundBitSet(), NbBits);
  }
  
  
  private static String toTribbleString(BitSet b) {
    StringBuilder sb = new StringBuilder();
    
    for(int i=0; i<NbBits/4; i++) {
      BitSet nibble = new BitSet(4);
      for(int j=0; j<4;j++) {
        nibble.set(j, b.get((i*4) + j));
      }
      sb.append(NibbleToString(nibble));
    }
    return sb.toString().trim();
  }
  
  @Override
  public String toString() {
    return toTribbleString(getGroundBitSet());
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
      if(NbBits%i == 0) { beatRhythms.addAll(Generate(i)); } 
    }
    
    for(var b : beatRhythms) { beatRhythmDict.put(b.toString(), b);}
  }
  
  public static BeatRhythm identifyBeatRhythm(Set<Integer> input) {
    return BeatRhythm.parseBeatRhythm(toTribbleString(new Combination(NbBits, input)));
  }
  
  public static BeatRhythm parseBeatRhythm(String s) {
    if(!beatRhythmDict.containsKey(s.trim())) throw new RuntimeException("not found");
    return beatRhythmDict.get(s.trim()); 
  }
  public static BeatRhythm fromGroundRhythm(Rhythm r) {
    return parseBeatRhythm(toTribbleString(r));
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
  
  private static String NibbleToString(BitSet b) {
    int intbyte = 0;
    
    for (int i = b.nextSetBit(0); i >= 0; i = b.nextSetBit(i + 1)) {
        intbyte += checkedPow(2, 3 - i);  
    }
    return String.format("%X", intbyte);
  }
  
  
  public BeatRhythm(BitSet b, int n) {
    super(b, n);
    if(NbBits%n != 0) throw new IllegalArgumentException("Unsupported division");
    bitlen = NbBits / getN();
  }

  public BeatRhythm(Integer n, Set<Integer> s) {
    super(n, s);
    if(NbBits%n != 0) throw new IllegalArgumentException("Unsupported division");
    bitlen = NbBits / getN();
  }

}
