package name.ncg777.maths.objects.sentences;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import name.ncg777.maths.objects.Combination;
import name.ncg777.maths.objects.Sequence;
import name.ncg777.maths.objects.words.BinaryWord;
import name.ncg777.maths.objects.words.HexadecimalWord;

public class HexadecimalSentence extends ArrayList<HexadecimalWord> implements Comparable<HexadecimalSentence>{
  private static final long serialVersionUID = 1L;

  public HexadecimalSentence(List<HexadecimalWord> m_l) {
    super();
    for (HexadecimalWord i : m_l) {
      this.add(i);
    }
  }

  public HexadecimalSentence() {
    super();
  }
  
//  public boolean isEquivalentUnderSyncronizedRotation(HexadecimalSentence other) {
//    if(other == null) return false;
//    if(this.size() != other.size()) return false;
//    
//    for(int i=0;i<this.size();i++) {
//      
//      HexadecimalSentence rot = HexadecimalSentence.rotate(other, i*16);
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
  
  public static ArrayList<HexadecimalSentence> fromBinarySentence(ArrayList<BinaryWord> list) {
    ArrayList<HexadecimalSentence> o = new ArrayList<>();
    
    for(BinaryWord r : list) {
      o.add(HexadecimalSentence.fromRhythm(r));
    }
    return o;
  }
  
//  public Sequence clusterPartition() {
//    ArrayList<HexadecimalSentence> clusters = HexadecimalSentence.clusterRhythmPartition(this.asRhythm().partitionByEquality());
//    ArrayList<BinaryWord> rs= new ArrayList<>();
//    for(HexadecimalSentence r : clusters) rs.add(r.asRhythm());
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
//    ArrayList<HexadecimalSentence> representants = new ArrayList<>();
//    TreeMap<String,TreeSet<HexadecimalSentence>> instances = new TreeMap<>();
//    
//    public void add(HexadecimalSentence item) {
//      boolean found = false;
//      for(HexadecimalSentence r : representants) {
//        if(r.isEquivalentUnderSyncronizedRotation(item)) {
//          found=true;
//          instances.get(r.toString()).add(item);
//        }
//      }
//      if(!found) {
//        TreeSet<HexadecimalSentence> inst = new TreeSet<>();
//        inst.add(item);
//        instances.put(item.toString(),inst);
//        representants.add(item);
//      }
//    }
//    public ArrayList<TreeSet<HexadecimalSentence>> getTreeSets() {
//      ArrayList<TreeSet<HexadecimalSentence>> o = new ArrayList<>();
//      o.addAll(instances.values());
//      return o;
//    }
//  }
  
//  public static ArrayList<HexadecimalSentence> clusterRhythmPartition(ArrayList<BinaryWord> _partition) {
//    if(_partition == null) throw new RuntimeException("clusterRhythmPartition:: partition is null.");
//    ArrayList<HexadecimalSentence> partition = HexadecimalSentence.fromRhythmArray(_partition);
//    if(partition.size()==1) {
//      ArrayList<HexadecimalSentence> f = new ArrayList<>();
//      f.add(partition.get(0));
//      return f;
//    }
//    
//    
//    RhythmHexaListUnionSet us = new RhythmHexaListUnionSet();
//    for(HexadecimalSentence r: partition) {us.add(r);}
//    ArrayList<HexadecimalSentence> o = new ArrayList<HexadecimalSentence>();
//    
//    for(TreeSet<HexadecimalSentence> t : us.getTreeSets()) {
//      HexadecimalSentence s = null;
//      for(HexadecimalSentence l : t) {
//        if(s==null) {s = (HexadecimalSentence)l.clone();}
//        s = HexadecimalSentence.or(s, l);
//      }
//      o.add(s);
//    }
//    
//    return CollectionUtils.reverse(o);
//  }
  
  public BinaryWord asBinaryWord(){
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
    return new BinaryWord(b,n);
  }
  static public HexadecimalSentence parseHexadecimalWord(String str) {
    String s = str.replace(" ", "");
    LinkedList<HexadecimalWord> output = new LinkedList<HexadecimalWord>();
    for (int i = 0; i < s.length() / 4; i++) {
      String tmp = s.substring(i * 4, (i + 1) * 4);
      tmp = tmp.substring(0, 2) + " " + tmp.substring(2, 4);
      output.add(HexadecimalWord.parse(tmp));
    }
    return new HexadecimalSentence(output);

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
  
  
  public static HexadecimalSentence fromRhythm(BinaryWord r){
    HexadecimalSentence output = new HexadecimalSentence();
    if(r.getN() % 16 != 0) {
      throw new RuntimeException("BinaryWord's size is not divisible by 16.");
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
      output.add(HexadecimalWord.tryConvert(t));
    }
    return output;
  }

  public static HexadecimalSentence not(HexadecimalSentence a) {
    HexadecimalSentence output = new HexadecimalSentence(a);

    for (int i = 0; i < output.size(); i++) {
      output.set(i, HexadecimalWord.not(output.get(i)));
    }
    return output;
  }

  public static HexadecimalSentence and(HexadecimalSentence a, HexadecimalSentence b) {
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

    HexadecimalSentence output = new HexadecimalSentence();
    for (int i = 0; i < n; i++) {
      output.add(HexadecimalWord.and(a.get(i), b.get(i)));
    }
    return output;
  }

  public static HexadecimalSentence or(HexadecimalSentence a, HexadecimalSentence b) {
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

    HexadecimalSentence output = new HexadecimalSentence();
    for (int i = 0; i < n; i++) {
      output.add(HexadecimalWord.or(a.get(i), b.get(i)));
    }
    return output;
  }

  public static HexadecimalSentence xor(HexadecimalSentence a, HexadecimalSentence b) {
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

    HexadecimalSentence output = new HexadecimalSentence();
    for (int i = 0; i < n; i++) {
      output.add(HexadecimalWord.xor(a.get(i), b.get(i)));
    }
    return output;
  }

  public static HexadecimalSentence minus(HexadecimalSentence a, HexadecimalSentence b) {
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

    HexadecimalSentence output = new HexadecimalSentence();
    for (int i = 0; i < n; i++) {
      output.add(HexadecimalWord.minus(a.get(i), b.get(i)));
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


  public static HexadecimalSentence convolve(HexadecimalSentence a, HexadecimalSentence b) {
    
    HexadecimalSentence carrier = a;
    HexadecimalSentence impulse = b;
    
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

    HexadecimalSentence output = new HexadecimalSentence();

    for (int i = 0; i < carrier.size(); i++) {
      TreeSet<Integer> t = new TreeSet<Integer>();

      for (int j = 0; j < 16; j++) {
        if (o[((i * 16) + j) % o.length]) {
          t.add(j);
        }
      }
      output.add(HexadecimalWord.tryConvert(t));
    }
    return output;

  }

  public HexadecimalSentence decimate(){
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
    List<HexadecimalWord> oo = new ArrayList<HexadecimalWord>();
    
    for(int i=0;i<rsz;i++) {
      oo.add(HexadecimalWord.tryConvert(new Combination(b[i],16)));
    }
    return new HexadecimalSentence(oo);
  }
  public static HexadecimalSentence expand(HexadecimalSentence a, int x, boolean fill) {
    int n = x;
    Boolean[] b = HexadecimalSentence.toBooleanArray(a);
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
    HexadecimalSentence output = new HexadecimalSentence();

    for (int i = 0; i < o.length / 16; i++) {
      TreeSet<Integer> t = new TreeSet<Integer>();

      for (int j = 0; j < 16; j++) {
        if (o[(i * 16) + j]) {
          t.add(j);
        }
      }
      output.add(HexadecimalWord.tryConvert(t));
    }
    return output;
  }

  public static Boolean[] toBooleanArray(HexadecimalSentence a) {

    Boolean output[] = new Boolean[a.size() * 16];
    for (int i = 0; i < a.size() * 16; i++) {
      output[i] = false;
    }

    for (int i = 0; i < a.size(); i++) {
      HexadecimalWord x = a.get(i);

      for (int j = x.nextSetBit(0); j >= 0; j = x.nextSetBit(j + 1)) {
        output[j + (i * 16)] = true;
      }
    }
    return output;
  }

  public static HexadecimalSentence juxt(HexadecimalSentence a, HexadecimalSentence b) {
    HexadecimalSentence output = new HexadecimalSentence();

    for (int i = 0; i < a.size(); i++) {
      output.add(a.get(i));
    }
    for (int i = 0; i < b.size(); i++) {
      output.add(b.get(i));
    }
    return output;
  }

  public void append(HexadecimalSentence a) {
    for (int i = 0; i < a.size(); i++) {
      this.add(a.get(i));
    }
  }

  @Override
  public int compareTo(HexadecimalSentence o) {
    return o.toString().compareTo(this.toString());
  }

}
