package name.ncg777.maths.objects;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public class WordHexaList extends LinkedList<WordHexa> implements Comparable<WordHexaList>{
  private static final long serialVersionUID = 1L;

  public WordHexaList(List<WordHexa> m_l) {
    super();
    for (WordHexa i : m_l) {
      this.add(i);
    }
  }

  public WordHexaList() {
    super();
  }
  
//  public boolean isEquivalentUnderSyncronizedRotation(WordHexaList other) {
//    if(other == null) return false;
//    if(this.size() != other.size()) return false;
//    
//    for(int i=0;i<this.size();i++) {
//      
//      WordHexaList rot = WordHexaList.rotate(other, i*16);
//      boolean eq = true;
//      for(int j=0;j<rot.size();j++) {
//        if(!this.get(j).toString().equals(rot.get(j).toString())) {
//          eq=false;
//          continue;
//        }
//      }
//      if(eq) return true;
//    }
//    return false;
//  }
  
  public static ArrayList<WordHexaList> fromRhythmArray(ArrayList<WordBinary> list) {
    ArrayList<WordHexaList> o = new ArrayList<>();
    
    for(WordBinary r : list) {
      o.add(WordHexaList.fromRhythm(r));
    }
    return o;
  }
  
//  public Sequence clusterPartition() {
//    ArrayList<WordHexaList> clusters = WordHexaList.clusterRhythmPartition(this.asRhythm().partitionByEquality());
//    ArrayList<WordBinary> rs= new ArrayList<>();
//    for(WordHexaList r : clusters) rs.add(r.asRhythm());
//    
//    Sequence o = new Sequence();
//    
//    int n = rs.get(0).getN();
//    
//    for(int i=0;i<n;i++) {
//      for(int j=0; j<clusters.size();j++) {
//        if(rs.get(j).get(i)) {
//          o.add(j);
//        }
//      }
//    }
//    return o;
//  }
  
//  private static class RhythmHexaListUnionSet {
//
//    ArrayList<WordHexaList> representants = new ArrayList<>();
//    TreeMap<String,TreeSet<WordHexaList>> instances = new TreeMap<>();
//    
//    public void add(WordHexaList item) {
//      boolean found = false;
//      for(WordHexaList r : representants) {
//        if(r.isEquivalentUnderSyncronizedRotation(item)) {
//          found=true;
//          instances.get(r.toString()).add(item);
//        }
//      }
//      if(!found) {
//        TreeSet<WordHexaList> inst = new TreeSet<>();
//        inst.add(item);
//        instances.put(item.toString(),inst);
//        representants.add(item);
//      }
//    }
//    public ArrayList<TreeSet<WordHexaList>> getTreeSets() {
//      ArrayList<TreeSet<WordHexaList>> o = new ArrayList<>();
//      o.addAll(instances.values());
//      return o;
//    }
//  }
  
//  public static ArrayList<WordHexaList> clusterRhythmPartition(ArrayList<WordBinary> _partition) {
//    if(_partition == null) throw new RuntimeException("clusterRhythmPartition:: partition is null.");
//    ArrayList<WordHexaList> partition = WordHexaList.fromRhythmArray(_partition);
//    if(partition.size()==1) {
//      ArrayList<WordHexaList> f = new ArrayList<>();
//      f.add(partition.get(0));
//      return f;
//    }
//    
//    
//    RhythmHexaListUnionSet us = new RhythmHexaListUnionSet();
//    for(WordHexaList r: partition) {us.add(r);}
//    ArrayList<WordHexaList> o = new ArrayList<WordHexaList>();
//    
//    for(TreeSet<WordHexaList> t : us.getTreeSets()) {
//      WordHexaList s = null;
//      for(WordHexaList l : t) {
//        if(s==null) {s = (WordHexaList)l.clone();}
//        s = WordHexaList.or(s, l);
//      }
//      o.add(s);
//    }
//    
//    return CollectionUtils.reverse(o);
//  }
  
  public WordBinary asBinaryWord(){
    int n = size()*16;
    BitSet b = new BitSet(n);
    for(int i=0;i<n;i++){
      int s = i / 16;
      try{
        b.set(i, this.get(s).get(i%16));
      }catch(Exception e){
        System.out.println("???");
      }
    }
    return new WordBinary(b,n);
  }
  static public WordHexaList parseHexadecimalWord(String str) {
    String s = str.replace(" ", "");
    LinkedList<WordHexa> output = new LinkedList<WordHexa>();
    for (int i = 0; i < s.length() / 4; i++) {
      String tmp = s.substring(i * 4, (i + 1) * 4);
      tmp = tmp.substring(0, 2) + " " + tmp.substring(2, 4);
      output.add(WordHexa.parseRhythmHexa(tmp));
    }
    return new WordHexaList(output);

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
  
  
  public static WordHexaList fromRhythm(WordBinary r){
    WordHexaList output = new WordHexaList();
    if(r.getN() % 16 != 0) {
      throw new RuntimeException("WordBinary's size is not divisible by 16.");
    }
    int k = 0;
    while(k<r.getN()) {
      TreeSet<Integer> t = new TreeSet<Integer>();
      for(int i=0;i<16;i++) {
        if(r.get(k)) {
          t.add(k%16);
        }
        k++;
      }
      output.add(WordHexa.identifyRhythm16(t));
    }
    return output;
  }

  public static WordHexaList not(WordHexaList a) {
    WordHexaList output = new WordHexaList(a);

    for (int i = 0; i < output.size(); i++) {
      output.set(i, WordHexa.not(output.get(i)));
    }
    return output;
  }

  public static WordHexaList and(WordHexaList a, WordHexaList b) {
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

    WordHexaList output = new WordHexaList();
    for (int i = 0; i < n; i++) {
      output.add(WordHexa.and(a.get(i), b.get(i)));
    }
    return output;
  }

  public static WordHexaList or(WordHexaList a, WordHexaList b) {
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

    WordHexaList output = new WordHexaList();
    for (int i = 0; i < n; i++) {
      output.add(WordHexa.or(a.get(i), b.get(i)));
    }
    return output;
  }

  public static WordHexaList xor(WordHexaList a, WordHexaList b) {
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

    WordHexaList output = new WordHexaList();
    for (int i = 0; i < n; i++) {
      output.add(WordHexa.xor(a.get(i), b.get(i)));
    }
    return output;
  }

  public static WordHexaList minus(WordHexaList a, WordHexaList b) {
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

    WordHexaList output = new WordHexaList();
    for (int i = 0; i < n; i++) {
      output.add(WordHexa.minus(a.get(i), b.get(i)));
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


  public static WordHexaList convolve(WordHexaList a, WordHexaList b) {
    
    WordHexaList carrier = a;
    WordHexaList impulse = b;
    
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

    WordHexaList output = new WordHexaList();

    for (int i = 0; i < carrier.size(); i++) {
      TreeSet<Integer> t = new TreeSet<Integer>();

      for (int j = 0; j < 16; j++) {
        if (o[((i * 16) + j) % o.length]) {
          t.add(j);
        }
      }
      output.add(WordHexa.identifyRhythm16(t));
    }
    return output;

  }

  public WordHexaList decimate(){
    Sequence S = this.asBinaryWord().getComposition().segment().get(0).asSequence();
    
    int rsz = this.size();
    BitSet[] b = new BitSet[rsz];
    for(int i=0;i<rsz;i++) {
      b[i] = new BitSet();
      b[i].or(this.get(i));
    }
    
    int t=0;
    int k=0;
    boolean keep = false;
    for(int i=0;i<rsz*16;i++) {
      if(t==0) {
        try{
          t = S.get(k++);
        } catch(IndexOutOfBoundsException ex) {
          break;
        }
        keep = true;
      }
      int rd = i/16;
      int rr = i%16;
      
      if(this.get(rd).get(rr)) {
        t--;
        if(keep) {
          keep = false;
        } else {
          b[rd].set(rr,false);
        }
      }
    }
    List<WordHexa> oo = new ArrayList<WordHexa>();
    
    for(int i=0;i<rsz;i++) {
      oo.add(WordHexa.identifyRhythmHexa(new Combination(b[i],16)));
    }
    return new WordHexaList(oo);
  }
  public static WordHexaList expand(WordHexaList a, int x, boolean fill) {
    int n = x;
    Boolean[] b = WordHexaList.toBooleanArray(a);
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
    WordHexaList output = new WordHexaList();

    for (int i = 0; i < o.length / 16; i++) {
      TreeSet<Integer> t = new TreeSet<Integer>();

      for (int j = 0; j < 16; j++) {
        if (o[(i * 16) + j]) {
          t.add(j);
        }
      }
      output.add(WordHexa.identifyRhythm16(t));
    }
    return output;
  }

  public static Boolean[] toBooleanArray(WordHexaList a) {

    Boolean output[] = new Boolean[a.size() * 16];
    for (int i = 0; i < a.size() * 16; i++) {
      output[i] = false;
    }

    for (int i = 0; i < a.size(); i++) {
      WordHexa x = a.get(i);

      for (int j = x.nextSetBit(0); j >= 0; j = x.nextSetBit(j + 1)) {
        output[j + (i * 16)] = true;
      }
    }
    return output;
  }

  public static WordHexaList juxt(WordHexaList a, WordHexaList b) {
    WordHexaList output = new WordHexaList();

    for (int i = 0; i < a.size(); i++) {
      output.add(a.get(i));
    }
    for (int i = 0; i < b.size(); i++) {
      output.add(b.get(i));
    }
    return output;
  }

  public void append(WordHexaList a) {
    for (int i = 0; i < a.size(); i++) {
      this.add(a.get(i));
    }
  }

  @Override
  public int compareTo(WordHexaList o) {
    return o.toString().compareTo(this.toString());
  }

}
