package name.ncg777.maths.objects;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import name.ncg777.computerScience.dataStructures.CollectionUtils;

public class WordOctalList extends LinkedList<WordOctal>  implements Comparable<WordOctalList>{
  private static final long serialVersionUID = 1L;

  public WordOctalList(List<WordOctal> m_l) {
    super();
    for (WordOctal i : m_l) {
      this.add(i);
    }
  }

  public WordOctalList() {
    super();
  }
  
  public WordBinary asBinary(){
    int n = size()*12;
    BitSet b = new BitSet(n);
    for(int i=0;i<n;i++){
      int s = i / 12;
      try{
        b.set(i, this.get(s).get(i%12));
      }catch(Exception e){
        System.out.println("???");
      }
    }
    return new WordBinary(b,n);
  }
  static public WordOctalList parseOctalWord(String str) {
    String s = str.replace(" ", "");
    LinkedList<WordOctal> output = new LinkedList<WordOctal>();
    for (int i = 0; i < s.length() / 4; i++) {
      String tmp = s.substring(i * 4, (i + 1) * 4);
      tmp = tmp.substring(0, 2) + " " + tmp.substring(2, 4);
      output.add(WordOctal.parseOctalWord(tmp));
    }
    return new WordOctalList(output);

  }

  public boolean isEquivalentUnderSyncronizedRotation(WordOctalList other) {
    if(other == null) return false;
    if(this.size() != other.size()) return false;
    
    for(int i=0;i<this.size();i++) {
      
      WordOctalList rot = WordOctalList.rotate(other, i*12);
      boolean eq = true;
      for(int j=0;j<rot.size();j++) {
        if(!this.get(j).toString().equals(rot.get(j).toString())) {
          eq=false;
          continue;
        }
      }
      if(eq) return true;
    }
    return false;
  }
  
  public static ArrayList<WordOctalList> fromRhythmArray(ArrayList<WordBinary> list) {
    ArrayList<WordOctalList> o = new ArrayList<>();
    
    for(WordBinary r : list) {
      o.add(WordOctalList.fromRhythm(r));
    }
    return o;
  }
  
  public Sequence clusterPartition() {
    ArrayList<WordOctalList> clusters = WordOctalList.clusterRhythmPartition(this.asBinary().partitionByEquality());
    ArrayList<WordBinary> rs= new ArrayList<>();
    for(WordOctalList r : clusters) rs.add(r.asBinary());
    
    Sequence o = new Sequence();
    
    int n = rs.get(0).getN();
    
    for(int i=0;i<n;i++) {
      for(int j=0; j<clusters.size();j++) {
        if(rs.get(j).get(i)) {
          o.add(j);
        }
      }
    }
    return o;
  }
  
  private static class R12ListUnionSet {

    ArrayList<WordOctalList> representants = new ArrayList<>();
    TreeMap<String,TreeSet<WordOctalList>> instances = new TreeMap<>();
    
    public void add(WordOctalList item) {
      boolean found = false;
      for(WordOctalList r : representants) {
        if(r.isEquivalentUnderSyncronizedRotation(item)) {
          found=true;
          instances.get(r.toString()).add(item);
        }
      }
      if(!found) {
        TreeSet<WordOctalList> inst = new TreeSet<>();
        inst.add(item);
        instances.put(item.toString(),inst);
        representants.add(item);
      }
    }
    public ArrayList<TreeSet<WordOctalList>> getTreeSets() {
      ArrayList<TreeSet<WordOctalList>> o = new ArrayList<>();
      o.addAll(instances.values());
      return o;
    }
  }
  
  public static ArrayList<WordOctalList> clusterRhythmPartition(ArrayList<WordBinary> _partition) {
    if(_partition == null) throw new RuntimeException("clusterRhythmPartition:: partition is null.");
    ArrayList<WordOctalList> partition = WordOctalList.fromRhythmArray(_partition);
    if(partition.size()==1) {
      ArrayList<WordOctalList> f = new ArrayList<>();
      f.add(partition.get(0));
      return f;
    }
    
    
    R12ListUnionSet us = new WordOctalList.R12ListUnionSet();
    for(WordOctalList r: partition) {us.add(r);}
    ArrayList<WordOctalList> o = new ArrayList<WordOctalList>();
    
    for(TreeSet<WordOctalList> t : us.getTreeSets()) {
      WordOctalList s = null;
      for(WordOctalList l : t) {
        if(s==null) {s = (WordOctalList)l.clone();}
        s = WordOctalList.or(s, l);
      }
      o.add(s);
    }
    return CollectionUtils.reverse(o);
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
  
  public static WordOctalList rotate(WordOctalList r, int t) {
    return fromRhythm(new WordBinary(r.asBinary().rotate(t), r.size()*12));
  }
  
  public static WordOctalList fromRhythm(WordBinary r){
    WordOctalList output = new WordOctalList();
    if(r.getN() % 12 != 0) {
      throw new RuntimeException("WordBinary's size is not divisible by 12.");
    }
    int k = 0;
    while(k<r.getN()) {
      TreeSet<Integer> t = new TreeSet<Integer>();
      for(int i=0;i<12;i++) {
        if(r.get(k)) {
          t.add(k%12);
        }
        k++;
      }
      output.add(WordOctal.identifyRhythm12(t));
    }
    return output;
  }

  public static WordOctalList not(WordOctalList a) {
    WordOctalList output = new WordOctalList(a);

    for (int i = 0; i < output.size(); i++) {
      output.set(i, WordOctal.not(output.get(i)));
    }
    return output;
  }

  public static WordOctalList and(WordOctalList a, WordOctalList b) {
    int n = (a.size() > b.size()) ? a.size() : b.size();
    int sza = a.size();
    int szb = b.size();

    for (int i = 0; i < n; i++) {
      if ((i + 1) > a.size()) {
        a.add(a.get(i % sza));
      }
      if ((i + 1) > b.size()) {
        b.add(b.get(i % szb));
      }
    }

    WordOctalList output = new WordOctalList();
    for (int i = 0; i < n; i++) {
      output.add(WordOctal.and(a.get(i), b.get(i)));
    }
    return output;
  }

  public static WordOctalList or(WordOctalList a, WordOctalList b) {
    int n = (a.size() > b.size()) ? a.size() : b.size();
    int sza = a.size();
    int szb = b.size();

    for (int i = 0; i < n; i++) {
      if ((i + 1) > a.size()) {
        a.add(a.get(i % sza));
      }
      if ((i + 1) > b.size()) {
        b.add(b.get(i % szb));
      }
    }

    WordOctalList output = new WordOctalList();
    for (int i = 0; i < n; i++) {
      output.add(WordOctal.or(a.get(i), b.get(i)));
    }
    return output;
  }

  public static WordOctalList xor(WordOctalList a, WordOctalList b) {
    int n = (a.size() > b.size()) ? a.size() : b.size();
    int sza = a.size();
    int szb = b.size();

    for (int i = 0; i < n; i++) {
      if ((i + 1) > a.size()) {
        a.add(a.get(i % sza));
      }
      if ((i + 1) > b.size()) {
        b.add(b.get(i % szb));
      }
    }

    WordOctalList output = new WordOctalList();
    for (int i = 0; i < n; i++) {
      output.add(WordOctal.xor(a.get(i), b.get(i)));
    }
    return output;
  }

  public static WordOctalList minus(WordOctalList a, WordOctalList b) {
    int n = (a.size() > b.size()) ? a.size() : b.size();
    int sza = a.size();
    int szb = b.size();

    for (int i = 0; i < n; i++) {
      if ((i + 1) > a.size()) {
        a.add(a.get(i % sza));
      }
      if ((i + 1) > b.size()) {
        b.add(b.get(i % szb));
      }
    }

    WordOctalList output = new WordOctalList();
    for (int i = 0; i < n; i++) {
      output.add(WordOctal.minus(a.get(i), b.get(i)));
    }
    return output;
  }
  
  public Integer getN() {
    int m = 0;
    for (int x = 0; x < this.size(); x++) {
      m += this.get(x).getN();
    }
    return m;
  }


  public static WordOctalList convolve(WordOctalList a, WordOctalList b) {
    
    WordOctalList carrier = a;
    WordOctalList impulse = b;
    
    Boolean[] b_carrier = toBooleanArray(carrier);
    Boolean[] b_impulse = toBooleanArray(impulse);

    Boolean[] o = new Boolean[b_carrier.length];

    for (int i = 0; i < o.length; i++) {
      o[i] = false;
    }

    for (int i = 0; i < b_carrier.length; i++) {
      for (int j = 0; j < b_impulse.length; j++) {
        o[(i + j) % o.length] = o[(i + j) % o.length] | (b_carrier[i] & b_impulse[j]);
      }
    }

    WordOctalList output = new WordOctalList();

    for (int i = 0; i < carrier.size(); i++) {
      TreeSet<Integer> t = new TreeSet<Integer>();

      for (int j = 0; j < 12; j++) {
        if (o[((i * 12) + j) % o.length]) {
          t.add(j);
        }
      }
      output.add(WordOctal.identifyRhythm12(t));
    }
    return output;

  }

  public WordOctalList decimate(){
    Sequence S = this.asBinary().getComposition().segment().get(0).asSequence();
    
    int rsz = this.size();
    BitSet[] b = new BitSet[rsz];
    for(int i=0;i<rsz;i++) {
      b[i] = new BitSet();
      b[i].or(this.get(i));
    }
    
    int t=0;
    int k=0;
    boolean keep = false;
    for(int i=0;i<rsz*12;i++) {
      if(t==0) {
        try{
          t = S.get(k++);
        } catch(IndexOutOfBoundsException ex) {
          break;
        }
        keep = true;
      }
      int rd = i/12;
      int rr = i%12;
      
      if(this.get(rd).get(rr)) {
        t--;
        if(keep) {
          keep = false;
        } else {
          b[rd].set(rr,false);
        }
      }
    }
    List<WordOctal> oo = new ArrayList<WordOctal>();
    
    for(int i=0;i<rsz;i++) {
      oo.add(WordOctal.identifyRhythm12(new Combination(b[i],12)));
    }
    return new WordOctalList(oo);
  }
  public static WordOctalList expand(WordOctalList a, int x, boolean fill) {
    int n = x;
    Boolean[] b = WordOctalList.toBooleanArray(a);
    Boolean[] o = new Boolean[n * b.length];
    for (int i = 0; i < o.length; i++) {
      o[i] = false;
    }
    for (int i = 0; i < b.length; i++) {
      o[i * n] = b[i];
      if(fill) {
        for(int j=1; j<n;j++) {
          o[(i * n) + j] = b[i];
        }
      }
    }
    WordOctalList output = new WordOctalList();

    for (int i = 0; i < o.length / 12; i++) {
      TreeSet<Integer> t = new TreeSet<Integer>();

      for (int j = 0; j < 12; j++) {
        if (o[(i * 12) + j]) {
          t.add(j);
        }
      }
      output.add(WordOctal.identifyRhythm12(t));
    }
    return output;
  }

  public static Boolean[] toBooleanArray(WordOctalList a) {

    Boolean output[] = new Boolean[a.size() * 12];
    for (int i = 0; i < a.size() * 12; i++) {
      output[i] = false;
    }

    for (int i = 0; i < a.size(); i++) {
      WordOctal x = a.get(i);

      for (int j = x.nextSetBit(0); j >= 0; j = x.nextSetBit(j + 1)) {
        output[j + (i * 12)] = true;
      }
    }
    return output;
  }

  public static WordOctalList juxt(WordOctalList a, WordOctalList b) {
    WordOctalList output = new WordOctalList();

    for (int i = 0; i < a.size(); i++) {
      output.add(a.get(i));
    }
    for (int i = 0; i < b.size(); i++) {
      output.add(b.get(i));
    }
    return output;
  }

  public void append(WordOctalList a) {
    for (int i = 0; i < a.size(); i++) {
      this.add(a.get(i));
    }
  }

  @Override
  public int compareTo(WordOctalList o) {
    return o.toString().compareTo(this.toString());
  }
}
