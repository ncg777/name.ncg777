package name.ncg777.maths.objects.sentences;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import name.ncg777.computerScience.dataStructures.CollectionUtils;
import name.ncg777.maths.objects.Sequence;
import name.ncg777.maths.objects.words.BinaryWord;
import name.ncg777.maths.objects.words.OctalWord;

public class OctalSentence extends ArrayList<OctalWord>  implements Comparable<OctalSentence>{
  private static final long serialVersionUID = 1L;

  public OctalSentence(List<OctalWord> m_l) {
    super();
    for (OctalWord i : m_l) {
      this.add(i);
    }
  }

  public OctalSentence() {
    super();
  }
  
  public BinaryWord asBinary(){
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
    return new BinaryWord(b,n);
  }
  static public OctalSentence parse(String str) {
    String[] strs = str.split("\\s+");
    LinkedList<OctalWord> output = new LinkedList<OctalWord>();
    for (var s : strs) {
      output.add(OctalWord.parse(s));
    }
    return new OctalSentence(output);

  }

  public boolean isEquivalentUnderSyncronizedRotation(OctalSentence other) {
    if(other == null) return false;
    if(this.size() != other.size()) return false;
    
    for(int i=0;i<this.size();i++) {
      
      OctalSentence rot = OctalSentence.rotate(other, i*12);
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
  
  public static ArrayList<OctalSentence> fromRhythmArray(ArrayList<BinaryWord> list) {
    ArrayList<OctalSentence> o = new ArrayList<>();
    
    for(BinaryWord r : list) {
      o.add(OctalSentence.fromRhythm(r));
    }
    return o;
  }
  
  public Sequence clusterPartition() {
    ArrayList<OctalSentence> clusters = OctalSentence.clusterRhythmPartition(this.asBinary().partitionByEquality());
    ArrayList<BinaryWord> rs= new ArrayList<>();
    for(OctalSentence r : clusters) rs.add(r.asBinary());
    
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
  
  private static class OctalSentenceUnionSet {

    ArrayList<OctalSentence> representants = new ArrayList<>();
    TreeMap<String,TreeSet<OctalSentence>> instances = new TreeMap<>();
    
    public void add(OctalSentence item) {
      boolean found = false;
      for(OctalSentence r : representants) {
        if(r.isEquivalentUnderSyncronizedRotation(item)) {
          found=true;
          instances.get(r.toString()).add(item);
        }
      }
      if(!found) {
        TreeSet<OctalSentence> inst = new TreeSet<>();
        inst.add(item);
        instances.put(item.toString(),inst);
        representants.add(item);
      }
    }
    public ArrayList<TreeSet<OctalSentence>> getTreeSets() {
      ArrayList<TreeSet<OctalSentence>> o = new ArrayList<>();
      o.addAll(instances.values());
      return o;
    }
  }
  
  public static ArrayList<OctalSentence> clusterRhythmPartition(ArrayList<BinaryWord> _partition) {
    if(_partition == null) throw new RuntimeException("clusterRhythmPartition:: partition is null.");
    ArrayList<OctalSentence> partition = OctalSentence.fromRhythmArray(_partition);
    if(partition.size()==1) {
      ArrayList<OctalSentence> f = new ArrayList<>();
      f.add(partition.get(0));
      return f;
    }
    
    
    OctalSentenceUnionSet us = new OctalSentence.OctalSentenceUnionSet();
    for(OctalSentence r: partition) {us.add(r);}
    ArrayList<OctalSentence> o = new ArrayList<OctalSentence>();
    
    for(TreeSet<OctalSentence> t : us.getTreeSets()) {
      OctalSentence s = null;
      for(OctalSentence l : t) {
        if(s==null) {s = (OctalSentence)l.clone();}
        s = OctalSentence.or(s, l);
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
  
  public static OctalSentence rotate(OctalSentence r, int t) {
    return fromRhythm(new BinaryWord(r.asBinary().rotate(t), r.size()*12));
  }
  
  public static OctalSentence fromRhythm(BinaryWord r){
    OctalSentence output = new OctalSentence();
    if(r.getN() % 12 != 0) {
      throw new RuntimeException("BinaryWord's size is not divisible by 12.");
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
      output.add(OctalWord.tryConvert(t));
    }
    return output;
  }

  public static OctalSentence not(OctalSentence a) {
    OctalSentence output = new OctalSentence(a);

    for (int i = 0; i < output.size(); i++) {
      output.set(i, OctalWord.not(output.get(i)));
    }
    return output;
  }

  public static OctalSentence and(OctalSentence a, OctalSentence b) {
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

    OctalSentence output = new OctalSentence();
    for (int i = 0; i < n; i++) {
      output.add(OctalWord.and(a.get(i), b.get(i)));
    }
    return output;
  }

  public static OctalSentence or(OctalSentence a, OctalSentence b) {
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

    OctalSentence output = new OctalSentence();
    for (int i = 0; i < n; i++) {
      output.add(OctalWord.or(a.get(i), b.get(i)));
    }
    return output;
  }

  public static OctalSentence xor(OctalSentence a, OctalSentence b) {
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

    OctalSentence output = new OctalSentence();
    for (int i = 0; i < n; i++) {
      output.add(OctalWord.xor(a.get(i), b.get(i)));
    }
    return output;
  }

  public static OctalSentence minus(OctalSentence a, OctalSentence b) {
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

    OctalSentence output = new OctalSentence();
    for (int i = 0; i < n; i++) {
      output.add(OctalWord.minus(a.get(i), b.get(i)));
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


  public static OctalSentence convolve(OctalSentence a, OctalSentence b) {
    
    OctalSentence carrier = a;
    OctalSentence impulse = b;
    
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

    OctalSentence output = new OctalSentence();

    for (int i = 0; i < carrier.size(); i++) {
      TreeSet<Integer> t = new TreeSet<Integer>();

      for (int j = 0; j < 12; j++) {
        if (o[((i * 12) + j) % o.length]) {
          t.add(j);
        }
      }
      output.add(OctalWord.tryConvert(t));
    }
    return output;

  }

//  public OctalSentence decimate(){
//    Sequence S = this.asBinary().getComposition().segment().get(0).asSequence();
//    
//    int rsz = this.size();
//    BitSet[] b = new BitSet[rsz];
//    for(int i=0;i<rsz;i++) {
//      b[i] = new BitSet();
//      b[i].or(this.get(i));
//    }
//    
//    int t=0;
//    int k=0;
//    boolean keep = false;
//    for(int i=0;i<rsz*12;i++) {
//      if(t==0) {
//        try{
//          t = S.get(k++);
//        } catch(IndexOutOfBoundsException ex) {
//          break;
//        }
//        keep = true;
//      }
//      int rd = i/12;
//      int rr = i%12;
//      
//      if(this.get(rd).get(rr)) {
//        t--;
//        if(keep) {
//          keep = false;
//        } else {
//          b[rd].set(rr,false);
//        }
//      }
//    }
//    List<OctalWord> oo = new ArrayList<OctalWord>();
//    
//    for(int i=0;i<rsz;i++) {
//      oo.add(OctalWord.tryConvert(new Combination(b[i],12)));
//    }
//    return new OctalSentence(oo);
//  }
//  public static OctalSentence expand(OctalSentence a, int x, boolean fill) {
//    int n = x;
//    Boolean[] b = OctalSentence.toBooleanArray(a);
//    Boolean[] o = new Boolean[n * b.length];
//    for (int i = 0; i < o.length; i++) {
//      o[i] = false;
//    }
//    for (int i = 0; i < b.length; i++) {
//      o[i * n] = b[i];
//      if(fill) {
//        for(int j=1; j<n;j++) {
//          o[(i * n) + j] = b[i];
//        }
//      }
//    }
//    OctalSentence output = new OctalSentence();
//
//    for (int i = 0; i < o.length / 12; i++) {
//      TreeSet<Integer> t = new TreeSet<Integer>();
//
//      for (int j = 0; j < 12; j++) {
//        if (o[(i * 12) + j]) {
//          t.add(j);
//        }
//      }
//      output.add(OctalWord.tryConvert(t));
//    }
//    return output;
//  }

  public static Boolean[] toBooleanArray(OctalSentence a) {

    Boolean output[] = new Boolean[a.size() * 12];
    for (int i = 0; i < a.size() * 12; i++) {
      output[i] = false;
    }

    for (int i = 0; i < a.size(); i++) {
      OctalWord x = a.get(i);

      for (int j = x.nextSetBit(0); j >= 0; j = x.nextSetBit(j + 1)) {
        output[j + (i * 12)] = true;
      }
    }
    return output;
  }

  public static OctalSentence juxt(OctalSentence a, OctalSentence b) {
    OctalSentence output = new OctalSentence();

    for (int i = 0; i < a.size(); i++) {
      output.add(a.get(i));
    }
    for (int i = 0; i < b.size(); i++) {
      output.add(b.get(i));
    }
    return output;
  }

  public void append(OctalSentence a) {
    for (int i = 0; i < a.size(); i++) {
      this.add(a.get(i));
    }
  }

  @Override
  public int compareTo(OctalSentence o) {
    return o.toString().compareTo(this.toString());
  }
}
