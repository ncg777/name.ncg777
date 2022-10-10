package name.ncg.Music;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public class BeatRhythmList extends LinkedList<BeatRhythm>  implements Comparable<BeatRhythmList>{
  private static final long serialVersionUID = 1L;

  public BeatRhythmList(List<BeatRhythm> m_l) {
    super();
    for (BeatRhythm i : m_l) {
      this.add(i);
    }
  }

  public BeatRhythmList() {
    super();
  }
  
  public Rhythm asRhythm(){
    int n = size()*BeatRhythm.Clicks;
    BitSet b = new BitSet(n);
    for(int i=0;i<n;i++){
      b.set(i, this.get(i / BeatRhythm.Clicks).get(i%BeatRhythm.Clicks));
    }
    return new Rhythm(b,n);
  }
  static public BeatRhythmList parseBeatRhythmSeq(String str) {
    String[] arr = str.trim().split("\\s+");
    int bytesInBeat = BeatRhythm.Clicks/8;
    if(arr.length%(bytesInBeat) != 0) throw new RuntimeException("segmentation error");
    BeatRhythmList l = new BeatRhythmList();
    int n = arr.length / (bytesInBeat);
    
    for(int i=0; i<n; i++) {
      StringBuilder sb = new StringBuilder();
      for(int j=0; j<bytesInBeat; j++) {
        sb.append(arr[(i*bytesInBeat)+j] + " ");
      }
      l.add(BeatRhythm.parseBeatRhythm(sb.toString()));
    }
    return l;
  }

  public boolean isEquivalentUnderSyncronizedRotation(BeatRhythmList other) {
    if(other == null) return false;
    if(this.size() != other.size()) return false;
    
    for(int i=0;i<this.size();i++) {
      
      BeatRhythmList rot = BeatRhythmList.rotate(other, i*BeatRhythm.Clicks);
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
  
  public static ArrayList<BeatRhythmList> fromRhythmArray(ArrayList<Rhythm> list) {
    ArrayList<BeatRhythmList> o = new ArrayList<>();
    
    for(Rhythm r : list) {
      o.add(BeatRhythmList.fromRhythm(r));
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
  
  public static BeatRhythmList rotate(BeatRhythmList r, int t) {
    return fromRhythm(new Rhythm(r.asRhythm().rotate(t), r.size()*BeatRhythm.Clicks));
  }
  
  public static BeatRhythmList fromRhythm(Rhythm r){
    BeatRhythmList output = new BeatRhythmList();
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

  public static Boolean[] toBooleanArray(BeatRhythmList a) {

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

  public static BeatRhythmList juxt(BeatRhythmList a, BeatRhythmList b) {
    BeatRhythmList output = new BeatRhythmList();

    for (int i = 0; i < a.size(); i++) {
      output.add(a.get(i));
    }
    for (int i = 0; i < b.size(); i++) {
      output.add(b.get(i));
    }
    return output;
  }

  public void append(BeatRhythmList a) {
    for (int i = 0; i < a.size(); i++) {
      this.add(a.get(i));
    }
  }

  @Override
  public int compareTo(BeatRhythmList o) {
    return o.toString().compareTo(this.toString());
  }
}
