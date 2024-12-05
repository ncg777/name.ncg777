package name.ncg777.maths.sentences;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

import name.ncg777.maths.words.BinaryWord;

public class BinarySentence extends ArrayList<BinaryWord>  implements Comparable<BinarySentence>{
  private static final long serialVersionUID = 1L;

  public BinarySentence(List<BinaryWord> m_l) {
    super();
    for (BinaryWord i : m_l) {
      this.add(i);
    }
  }

  public BinarySentence() {
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
  static public BinarySentence parse(String str) {
    String[] strs = str.split("\\s+");
    
    LinkedList<BinaryWord> output = new LinkedList<BinaryWord>();
    for (var s : strs) {
      output.add(BinaryWord.parse(s));
    }
    return new BinarySentence(output);

  }

//  public boolean isEquivalentUnderSyncronizedRotation(BinarySentence other) {
//    if(other == null) return false;
//    if(this.size() != other.size()) return false;
//    
//    for(int i=0;i<this.size();i++) {
//      
//      BinarySentence rot = BinarySentence.rotate(other, i*12);
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
  
  
//  public Sequence clusterPartition() {
//    ArrayList<BinarySentence> clusters = BinarySentence.clusterRhythmPartition(this.asBinary().partitionByEquality());
//    ArrayList<BinaryWord> rs= new ArrayList<>();
//    for(BinarySentence r : clusters) rs.add(r.asBinary());
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
  
//  private static class BinarySentenceUnionSet {
//
//    ArrayList<BinarySentence> representants = new ArrayList<>();
//    TreeMap<String,TreeSet<BinarySentence>> instances = new TreeMap<>();
//    
//    public void add(BinarySentence item) {
//      boolean found = false;
//      for(BinarySentence r : representants) {
//        if(r.isEquivalentUnderSyncronizedRotation(item)) {
//          found=true;
//          instances.get(r.toString()).add(item);
//        }
//      }
//      if(!found) {
//        TreeSet<BinarySentence> inst = new TreeSet<>();
//        inst.add(item);
//        instances.put(item.toString(),inst);
//        representants.add(item);
//      }
//    }
//    public ArrayList<TreeSet<BinarySentence>> getTreeSets() {
//      ArrayList<TreeSet<BinarySentence>> o = new ArrayList<>();
//      o.addAll(instances.values());
//      return o;
//    }
//  }
//  
//  public static ArrayList<BinarySentence> clusterRhythmPartition(BinarySentence _partition) {
//    if(_partition == null) throw new RuntimeException("clusterRhythmPartition:: partition is null.");
//    ArrayList<BinarySentence> partition = BinarySentence.fromRhythmArray(_partition);
//    if(partition.size()==1) {
//      ArrayList<BinarySentence> f = new ArrayList<>();
//      f.add(partition.get(0));
//      return f;
//    }
//    
//    
//    BinarySentenceUnionSet us = new BinarySentence.BinarySentenceUnionSet();
//    for(BinarySentence r: partition) {us.add(r);}
//    ArrayList<BinarySentence> o = new ArrayList<BinarySentence>();
//    
//    for(TreeSet<BinarySentence> t : us.getTreeSets()) {
//      BinarySentence s = null;
//      for(BinarySentence l : t) {
//        if(s==null) {s = (BinarySentence)l.clone();}
//        s = BinarySentence.or(s, l);
//      }
//      o.add(s);
//    }
//    return CollectionUtils.reverse(o);
//  }
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
  
//  public static BinarySentence rotate(BinarySentence r, int t) {
//    return new BinaryWord(r.asBinary().rotate(t), r.size()*12);
//  }
//  
//  public static BinarySentence not(BinarySentence a) {
//    BinarySentence output = new BinarySentence(a);
//
//    for (int i = 0; i < output.size(); i++) {
//      output.set(i, BinaryWord.not(output.get(i)));
//    }
//    return output;
//  }
//
//  public static BinarySentence and(BinarySentence a, BinarySentence b) {
//    int n = (a.size() > b.size()) ? a.size() : b.size();
//    int sza = a.size();
//    int szb = b.size();
//
//    for (int i = 0; i < n; i++) {
//      if ((i + 1) > a.size()) {
//        a.add(a.get(i % sza));
//      }
//      if ((i + 1) > b.size()) {
//        b.add(b.get(i % szb));
//      }
//    }
//
//    BinarySentence output = new BinarySentence();
//    for (int i = 0; i < n; i++) {
//      output.add(BinaryWord.and(a.get(i), b.get(i)));
//    }
//    return output;
//  }
//
//  public static BinarySentence or(BinarySentence a, BinarySentence b) {
//    int n = (a.size() > b.size()) ? a.size() : b.size();
//    int sza = a.size();
//    int szb = b.size();
//
//    for (int i = 0; i < n; i++) {
//      if ((i + 1) > a.size()) {
//        a.add(a.get(i % sza));
//      }
//      if ((i + 1) > b.size()) {
//        b.add(b.get(i % szb));
//      }
//    }
//
//    BinarySentence output = new BinarySentence();
//    for (int i = 0; i < n; i++) {
//      output.add(BinaryWord.or(a.get(i), b.get(i)));
//    }
//    return output;
//  }
//
//  public static BinarySentence xor(BinarySentence a, BinarySentence b) {
//    int n = (a.size() > b.size()) ? a.size() : b.size();
//    int sza = a.size();
//    int szb = b.size();
//
//    for (int i = 0; i < n; i++) {
//      if ((i + 1) > a.size()) {
//        a.add(a.get(i % sza));
//      }
//      if ((i + 1) > b.size()) {
//        b.add(b.get(i % szb));
//      }
//    }
//
//    BinarySentence output = new BinarySentence();
//    for (int i = 0; i < n; i++) {
//      output.add(BinaryWord.xor(a.get(i), b.get(i)));
//    }
//    return output;
//  }
//
//  public static BinarySentence minus(BinarySentence a, BinarySentence b) {
//    int n = (a.size() > b.size()) ? a.size() : b.size();
//    int sza = a.size();
//    int szb = b.size();
//
//    for (int i = 0; i < n; i++) {
//      if ((i + 1) > a.size()) {
//        a.add(a.get(i % sza));
//      }
//      if ((i + 1) > b.size()) {
//        b.add(b.get(i % szb));
//      }
//    }
//
//    BinarySentence output = new BinarySentence();
//    for (int i = 0; i < n; i++) {
//      output.add(BinaryWord.minus(a.get(i), b.get(i)));
//    }
//    return output;
//  }
  
  public Integer getNumberOfCharacters() {
    int m = 0;
    for (int x = 0; x < this.size(); x++) {
      m += this.get(x).getN();
    }
    return m;
  }

//  public static BinarySentence convolve(BinarySentence a, BinarySentence b) {
//    BinarySentence carrier = a;
//    BinarySentence impulse = b;
//    
//    Boolean[] b_carrier = toBooleanArray(carrier);
//    Boolean[] b_impulse = toBooleanArray(impulse);
//
//    Boolean[] o = new Boolean[b_carrier.length];
//
//    for (int i = 0; i < o.length; i++) {
//      o[i] = false;
//    }
//
//    for (int i = 0; i < b_carrier.length; i++) {
//      for (int j = 0; j < b_impulse.length; j++) {
//        o[(i + j) % o.length] = o[(i + j) % o.length] | (b_carrier[i] & b_impulse[j]);
//      }
//    }
//
//    BinarySentence output = new BinarySentence();
//
//    for (int i = 0; i < carrier.size(); i++) {
//      TreeSet<Integer> t = new TreeSet<Integer>();
//
//      for (int j = 0; j < 12; j++) {
//        if (o[((i * 2) + j) % o.length]) {
//          t.add(j);
//        }
//      }
//      output.add(BinaryWord.tryConvert(t));
//    }
//    return output;
//
//  }

  public static Boolean[] toBooleanArray(BinarySentence a) {

    Boolean output[] = new Boolean[a.size() * 2];
    for (int i = 0; i < a.size() * 2; i++) {
      output[i] = false;
    }

    for (int i = 0; i < a.size(); i++) {
      BinaryWord x = a.get(i);

      for (int j = x.nextSetBit(0); j >= 0; j = x.nextSetBit(j + 1)) {
        output[j + (i * 2)] = true;
      }
    }
    return output;
  }

  public static BinarySentence juxtapose(BinarySentence a, BinarySentence b) {
    BinarySentence output = new BinarySentence();

    for (int i = 0; i < a.size(); i++) {
      output.add(a.get(i));
    }
    for (int i = 0; i < b.size(); i++) {
      output.add(b.get(i));
    }
    return output;
  }

  public void append(BinarySentence a) {
    for (int i = 0; i < a.size(); i++) {
      this.add(a.get(i));
    }
  }

  @Override
  public int compareTo(BinarySentence o) {
    return o.toString().compareTo(this.toString());
  }
}
