package name.ncg777.maths.sentences;

import java.util.List;

import com.google.common.base.Joiner;

import name.ncg777.maths.words.Alphabet;
import name.ncg777.maths.words.Digraph;

public class DigraphSentence extends Sentence<Digraph> {
  private static final long serialVersionUID = 1L;
 
  public DigraphSentence(Alphabet alphabet, List<Digraph> m_l) {
    super(alphabet);
    for (Digraph i : m_l) {
      this.add(i);
    }
  }

  public DigraphSentence(Alphabet alphabet) {
    super(alphabet);
  }

  public DigraphSentence(Alphabet alphabet, String string) {
    super(alphabet);
    string = string.replaceAll("\\s+", string);
    if(string.length()%2!=0) throw new IllegalArgumentException();
    
    for(int i=0;i<string.length()/2;i++) {
      this.add(new Digraph(alphabet, string.substring(i*2,(i*2)+2)));
    }
  }

//  public boolean isEquivalentUnderSyncronizedRotation(DigraphSentence other) {
//    if(other == null) return false;
//    if(this.size() != other.size()) return false;
//    
//    for(int i=0;i<this.size();i++) {
//      
//      DigraphSentence rot = DigraphSentence.rotate(other, i*12);
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
//    ArrayList<DigraphSentence> clusters = DigraphSentence.clusterRhythmPartition(this.asBinary().partitionByEquality());
//    ArrayList<Digraph> rs= new ArrayList<>();
//    for(DigraphSentence r : clusters) rs.add(r.asBinary());
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
  
//  private static class DigraphSentenceUnionSet {
//
//    ArrayList<DigraphSentence> representants = new ArrayList<>();
//    TreeMap<String,TreeSet<DigraphSentence>> instances = new TreeMap<>();
//    
//    public void add(DigraphSentence item) {
//      boolean found = false;
//      for(DigraphSentence r : representants) {
//        if(r.isEquivalentUnderSyncronizedRotation(item)) {
//          found=true;
//          instances.get(r.toString()).add(item);
//        }
//      }
//      if(!found) {
//        TreeSet<DigraphSentence> inst = new TreeSet<>();
//        inst.add(item);
//        instances.put(item.toString(),inst);
//        representants.add(item);
//      }
//    }
//    public ArrayList<TreeSet<DigraphSentence>> getTreeSets() {
//      ArrayList<TreeSet<DigraphSentence>> o = new ArrayList<>();
//      o.addAll(instances.values());
//      return o;
//    }
//  }
//  
//  public static ArrayList<DigraphSentence> clusterRhythmPartition(DigraphSentence _partition) {
//    if(_partition == null) throw new RuntimeException("clusterRhythmPartition:: partition is null.");
//    ArrayList<DigraphSentence> partition = DigraphSentence.fromRhythmArray(_partition);
//    if(partition.size()==1) {
//      ArrayList<DigraphSentence> f = new ArrayList<>();
//      f.add(partition.get(0));
//      return f;
//    }
//    
//    
//    DigraphSentenceUnionSet us = new DigraphSentence.DigraphSentenceUnionSet();
//    for(DigraphSentence r: partition) {us.add(r);}
//    ArrayList<DigraphSentence> o = new ArrayList<DigraphSentence>();
//    
//    for(TreeSet<DigraphSentence> t : us.getTreeSets()) {
//      DigraphSentence s = null;
//      for(DigraphSentence l : t) {
//        if(s==null) {s = (DigraphSentence)l.clone();}
//        s = DigraphSentence.or(s, l);
//      }
//      o.add(s);
//    }
//    return CollectionUtils.reverse(o);
//  }
  @Override
  public String toString() {
    return Joiner.on(" ").join(this);
  }
  
//  public static DigraphSentence rotate(DigraphSentence r, int t) {
//    return new Digraph(r.asBinary().rotate(t), r.size()*12);
//  }
//  
//  public static DigraphSentence not(DigraphSentence a) {
//    DigraphSentence output = new DigraphSentence(a);
//
//    for (int i = 0; i < output.size(); i++) {
//      output.set(i, Digraph.not(output.get(i)));
//    }
//    return output;
//  }
//
//  public static DigraphSentence and(DigraphSentence a, DigraphSentence b) {
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
//    DigraphSentence output = new DigraphSentence();
//    for (int i = 0; i < n; i++) {
//      output.add(Digraph.and(a.get(i), b.get(i)));
//    }
//    return output;
//  }
//
//  public static DigraphSentence or(DigraphSentence a, DigraphSentence b) {
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
//    DigraphSentence output = new DigraphSentence();
//    for (int i = 0; i < n; i++) {
//      output.add(Digraph.or(a.get(i), b.get(i)));
//    }
//    return output;
//  }
//
//  public static DigraphSentence xor(DigraphSentence a, DigraphSentence b) {
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
//    DigraphSentence output = new DigraphSentence();
//    for (int i = 0; i < n; i++) {
//      output.add(Digraph.xor(a.get(i), b.get(i)));
//    }
//    return output;
//  }
//
//  public static DigraphSentence minus(DigraphSentence a, DigraphSentence b) {
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
//    DigraphSentence output = new DigraphSentence();
//    for (int i = 0; i < n; i++) {
//      output.add(Digraph.minus(a.get(i), b.get(i)));
//    }
//    return output;
//  }
  
//  public Integer getNumberOfCharacters() {
//    int m = 0;
//    for (int x = 0; x < this.size(); x++) {
//      m += this.get(x).getN();
//    }
//    return m;
//  }

//  public static DigraphSentence convolve(DigraphSentence a, DigraphSentence b) {
//    DigraphSentence carrier = a;
//    DigraphSentence impulse = b;
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
//    DigraphSentence output = new DigraphSentence();
//
//    for (int i = 0; i < carrier.size(); i++) {
//      TreeSet<Integer> t = new TreeSet<Integer>();
//
//      for (int j = 0; j < 12; j++) {
//        if (o[((i * 2) + j) % o.length]) {
//          t.add(j);
//        }
//      }
//      output.add(Digraph.tryConvert(t));
//    }
//    return output;
//
//  }
//
//  public static Boolean[] toBooleanArray(DigraphSentence a) {
//
//    Boolean output[] = new Boolean[a.size() * 2];
//    for (int i = 0; i < a.size() * 2; i++) {
//      output[i] = false;
//    }
//
//    for (int i = 0; i < a.size(); i++) {
//      Digraph x = a.get(i);
//
//      for (int j = x.nextSetBit(0); j >= 0; j = x.nextSetBit(j + 1)) {
//        output[j + (i * 2)] = true;
//      }
//    }
//    return output;
//  }
//
//  public static DigraphSentence juxtapose(DigraphSentence a, DigraphSentence b) {
//    DigraphSentence output = new DigraphSentence();
//
//    for (int i = 0; i < a.size(); i++) {
//      output.add(a.get(i));
//    }
//    for (int i = 0; i < b.size(); i++) {
//      output.add(b.get(i));
//    }
//    return output;
//  }
//
//  public void append(DigraphSentence a) {
//    for (int i = 0; i < a.size(); i++) {
//      this.add(a.get(i));
//    }
//  }
//
//  @Override
//  public int compareTo(DigraphSentence o) {
//    return o.toString().compareTo(this.toString());
//  }
}
