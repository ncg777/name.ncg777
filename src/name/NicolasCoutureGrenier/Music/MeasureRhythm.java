package name.NicolasCoutureGrenier.Music;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.TreeSet;

import name.NicolasCoutureGrenier.Maths.Enumerations.MixedRadixEnumeration;

public class MeasureRhythm extends ArrayList<BeatRhythm>  implements Comparable<MeasureRhythm>, Serializable{
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
    int n = size()*BeatRhythm.NbBits;
    BitSet b = new BitSet(n);
    var arr = new ArrayList<Rhythm>();
    for(var br : this) arr.add(br.getGroundRhythm());
    for(int i=0;i<n;i++){
      b.set(i, arr.get(i / BeatRhythm.NbBits).get(i%BeatRhythm.NbBits));
    }
    return new Rhythm(b,n);
  }
  
  public static TreeSet<MeasureRhythm> generate(int nbBeats) {
    ArrayList<BeatRhythm> brs = new ArrayList<BeatRhythm>();
    brs.addAll(BeatRhythm.getBeatRhythms());
    var brs_sz = brs.size();
    Integer base[] = new Integer[nbBeats];
    int total = 1;
    for(int i=0;i<nbBeats;i++) { base[i] = brs_sz; total*=base[i]; }
    var mre = new MixedRadixEnumeration(base);
    
    var o =new TreeSet<MeasureRhythm>();
    int tmp = 0;
    while(mre.hasMoreElements()) {
      Integer[] coord = mre.nextElement();
      var a = new ArrayList<BeatRhythm>();
      for(int i=0; i<nbBeats;i++) {
        a.add(brs.get(coord[i]));
      }
      o.add(new MeasureRhythm(a));
      System.out.println(tmp++ + " / " + total);
    };
    return o;
  }
  
  static public MeasureRhythm parseMeasureRhythm(String str) {
    String[] arr = str.trim().split("\\s+");
    MeasureRhythm l = new MeasureRhythm();
    int n = arr.length;
    
    for(int i=0; i<n; i++) {
      l.add(BeatRhythm.parseBeatRhythm(arr[i]));
    }
    return l;
  }
  
  public ArrayList<MeasureRhythm> splitInChunks(int n) {
    if(this.size()%n != 0) throw new RuntimeException("segmentation error");
    
    var o = new ArrayList<MeasureRhythm>();
    
    for(int i=0;i<this.size()/n;i++) {
      var t = new MeasureRhythm();
      for(int j=0; j<n;j++) {
        t.add(this.get((i*n)+j));
      }
      
      o.add(t);
    }
    return o;
  }

  public boolean isEquivalentUnderSyncronizedRotation(MeasureRhythm other) {
    if(other == null) return false;
    if(this.size() != other.size()) return false;
    
    for(int i=0;i<this.size();i++) {
      
      MeasureRhythm rot = MeasureRhythm.rotate(other, i*BeatRhythm.NbBits);
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
      o.add(BeatRhythm.fromGroundRhythm(r));
    }
    return o;
  }
    
  @Override
  public String toString() {
    StringBuilder t = new StringBuilder();
    int n = this.size();

    for (int i = 0; i < n; i++) {
      t.append(this.get(i).toString());
      if (i != (n - 1)) {
        t.append(" ");
      }
    }
    return t.toString();
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
    
    
    return this.toString().equals(((MeasureRhythm)obj).toString());
  }

  public static MeasureRhythm rotate(MeasureRhythm r, int t) {
    return fromRhythm(new Rhythm(r.asRhythm().rotate(t), r.size()*BeatRhythm.NbBits));
  }
  
  public static MeasureRhythm fromRhythm(Rhythm r){
    MeasureRhythm output = new MeasureRhythm();
    if(r.getN() % BeatRhythm.NbBits != 0) {
      throw new RuntimeException("Rhythm's size is not divided by BeatRhythm.NbBits.");
    }
    int k = 0;
    while(k<r.getN()) {
      TreeSet<Integer> t = new TreeSet<Integer>();
      for(int i=0;i<BeatRhythm.NbBits;i++) {
        if(r.get(k)) {
          t.add(k%BeatRhythm.NbBits);
        }
        k++;
      }
      output.add(BeatRhythm.identifyBeatRhythm(t));
    }
    return output;
  }
 
  public Integer getN() {
    return this.size()*BeatRhythm.NbBits;
  }

  public static Boolean[] toBooleanArray(MeasureRhythm a) {

    Boolean output[] = new Boolean[a.size() * BeatRhythm.NbBits];
    for (int i = 0; i < a.size() * BeatRhythm.NbBits; i++) {
      output[i] = false;
    }

    for (int i = 0; i < a.size(); i++) {
      Rhythm x = a.get(i).getGroundRhythm();

      for (int j = x.nextSetBit(0); j >= 0; j = x.nextSetBit(j + 1)) {
        output[j + (i * BeatRhythm.NbBits)] = true;
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
