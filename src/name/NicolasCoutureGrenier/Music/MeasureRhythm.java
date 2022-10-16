package name.NicolasCoutureGrenier.Music;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import name.NicolasCoutureGrenier.Maths.Enumerations.MixedRadixEnumeration;

public class MeasureRhythm extends LinkedList<BeatRhythm>  implements Comparable<MeasureRhythm>{
  private static final long serialVersionUID = 1L;

  public MeasureRhythm(List<BeatRhythm> m_l) {
    super();
    for (BeatRhythm i : m_l) {
      this.add(i);
    }
  }

  public MeasureRhythm() {
    super();
  }
  
  public Rhythm asRhythm(){
    int n = size()*BeatRhythm.Clicks;
    BitSet b = new BitSet(n);
    var arr = new ArrayList<Rhythm>();
    for(var br : this) arr.add(br.getRhythmClicks());
    for(int i=0;i<n;i++){
      b.set(i, arr.get(i / BeatRhythm.Clicks).get(i%BeatRhythm.Clicks));
    }
    return new Rhythm(b,n);
  }
  
  public static TreeSet<MeasureRhythm> generate(int nbBeats) {
    ArrayList<Rhythm> brs = BeatRhythm.getBeatRhythms().stream().map(b -> b.getRhythmClicks())
        .collect(Collectors.toCollection(ArrayList<Rhythm>::new));
    var brs_sz = brs.size();
    Integer base[] = new Integer[nbBeats];
    for(int i=0;i<nbBeats;i++) { base[i] = brs_sz; }
    var mre = new MixedRadixEnumeration(base);
    
    TreeSet<MeasureRhythm> o = new TreeSet<>();
    
    while(mre.hasMoreElements()) {
      var c = mre.nextElement();
      var a = new ArrayList<Rhythm>();
      for(int i=0; i<nbBeats;i++) {
        a.add(brs.get(c[i]));
      }
      o.add(MeasureRhythm.fromRhythmArray(a));
    }
    return o;
  }
  
  static public MeasureRhythm parseMeasureRhythm(String str) {
    String[] arr = str.trim().split("\\s+");
    int bytesInBeat = BeatRhythm.Clicks/8;
    if(arr.length%(bytesInBeat) != 0) throw new RuntimeException("segmentation error");
    MeasureRhythm l = new MeasureRhythm();
    int n = arr.length / (bytesInBeat);
    
    for(int i=0; i<n; i++) {
      StringBuilder sb = new StringBuilder();
      for(int j=0; j<bytesInBeat; j++) {
        sb.append(arr[(i*bytesInBeat)+j] + " ");
      }
      l.add(BeatRhythm.parseBeatRhythm(sb.toString().trim()));
    }
    return l;
  }
  
  public ArrayList<MeasureRhythm> splitInPairs() {
    if(this.size()%2 != 0) throw new RuntimeException("segmentation error");
    
    var o = new ArrayList<MeasureRhythm>();
    
    for(int i=0;i<this.size()/2;i++) {
      var t = new MeasureRhythm();
      t.add(this.get(i*2));
      t.add(this.get((i*2)+1));
      o.add(t);
    }
    return o;
  }

  public boolean isEquivalentUnderSyncronizedRotation(MeasureRhythm other) {
    if(other == null) return false;
    if(this.size() != other.size()) return false;
    
    for(int i=0;i<this.size();i++) {
      
      MeasureRhythm rot = MeasureRhythm.rotate(other, i*BeatRhythm.Clicks);
      boolean eq = true;
      for(int j=0;j<rot.size();j++) {
        if(!this.get(j).equals(rot.get(j))) {
          eq=false;
          continue;
        }
      }
      if(eq) return true;
    }
    return false;
  }
  
  public static MeasureRhythm fromRhythmArray(ArrayList<Rhythm> list) {
    MeasureRhythm o = new MeasureRhythm();
    
    for(Rhythm r : list) {
      o.add(BeatRhythm.fromRhythmClicks(r));
    }
    return o;
  }
    
  @Override
  public String toString() {
    String t = "";
    int n = this.size();

    for (int i = 0; i < n; i++) {
      t += this.get(i).toString();
      if (i != (n - 1)) {
        t += " ";
      }
    }
    return t;
  }
  
  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    return toString().equals(((MeasureRhythm)obj).toString());
  }

  public static MeasureRhythm rotate(MeasureRhythm r, int t) {
    return fromRhythm(new Rhythm(r.asRhythm().rotate(t), r.size()*BeatRhythm.Clicks));
  }
  
  public static MeasureRhythm fromRhythm(Rhythm r){
    MeasureRhythm output = new MeasureRhythm();
    if(r.getN() % BeatRhythm.Clicks != 0) {
      throw new RuntimeException("Rhythm's size is not divided by BeatRhythm.Clicks.");
    }
    int k = 0;
    while(k<r.getN()) {
      TreeSet<Integer> t = new TreeSet<Integer>();
      for(int i=0;i<BeatRhythm.Clicks;i++) {
        if(r.get(k)) {
          t.add(k%BeatRhythm.Clicks);
        }
        k++;
      }
      output.add(BeatRhythm.identifyBeatRhythm(t));
    }
    return output;
  }
 
  public Integer getN() {
    return this.size()*BeatRhythm.Clicks;
  }

  public static Boolean[] toBooleanArray(MeasureRhythm a) {

    Boolean output[] = new Boolean[a.size() * BeatRhythm.Clicks];
    for (int i = 0; i < a.size() * BeatRhythm.Clicks; i++) {
      output[i] = false;
    }

    for (int i = 0; i < a.size(); i++) {
      Rhythm x = a.get(i).getRhythmClicks();

      for (int j = x.nextSetBit(0); j >= 0; j = x.nextSetBit(j + 1)) {
        output[j + (i * BeatRhythm.Clicks)] = true;
      }
    }
    return output;
  }

  public static MeasureRhythm juxt(MeasureRhythm a, MeasureRhythm b) {
    MeasureRhythm output = new MeasureRhythm();

    for (int i = 0; i < a.size(); i++) {
      output.add(a.get(i));
    }
    for (int i = 0; i < b.size(); i++) {
      output.add(b.get(i));
    }
    return output;
  }

  public void append(MeasureRhythm a) {
    for (int i = 0; i < a.size(); i++) {
      this.add(a.get(i));
    }
  }

  @Override
  public int compareTo(MeasureRhythm o) {
    return o.toString().compareTo(this.toString());
  }
}
