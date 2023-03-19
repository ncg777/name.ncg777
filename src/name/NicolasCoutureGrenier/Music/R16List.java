package name.NicolasCoutureGrenier.Music;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import name.NicolasCoutureGrenier.Maths.DataStructures.CollectionUtils;
import name.NicolasCoutureGrenier.Maths.DataStructures.Combination;
import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;

public class R16List extends LinkedList<Rhythm16> implements Comparable<R16List>{
  private static final long serialVersionUID = 1L;

  public R16List(List<Rhythm16> m_l) {
    super();
    for (Rhythm16 i : m_l) {
      this.add(i);
    }
  }

  public R16List() {
    super();
  }
  
  public boolean isEquivalentUnderSyncronizedRotation(R16List other) {
    if(other == null) return false;
    if(this.size() != other.size()) return false;
    
    for(int i=0;i<this.size();i++) {
      
      R16List rot = R16List.rotate(other, i*16);
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
  
  public static ArrayList<R16List> fromRhythmArray(ArrayList<Rhythm> list) {
    ArrayList<R16List> o = new ArrayList<>();
    
    for(Rhythm r : list) {
      o.add(R16List.fromRhythm(r));
    }
    return o;
  }
  
  public Sequence clusterPartition() {
    ArrayList<R16List> clusters = R16List.clusterRhythmPartition(this.asRhythm().partitionByEquality());
    ArrayList<Rhythm> rs= new ArrayList<>();
    for(R16List r : clusters) rs.add(r.asRhythm());
    
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
  
  private static class R16ListUnionSet {

    ArrayList<R16List> representants = new ArrayList<>();
    TreeMap<String,TreeSet<R16List>> instances = new TreeMap<>();
    
    public void add(R16List item) {
      boolean found = false;
      for(R16List r : representants) {
        if(r.isEquivalentUnderSyncronizedRotation(item)) {
          found=true;
          instances.get(r.toString()).add(item);
        }
      }
      if(!found) {
        TreeSet<R16List> inst = new TreeSet<>();
        inst.add(item);
        instances.put(item.toString(),inst);
        representants.add(item);
      }
    }
    public ArrayList<TreeSet<R16List>> getTreeSets() {
      ArrayList<TreeSet<R16List>> o = new ArrayList<>();
      o.addAll(instances.values());
      return o;
    }
  }
  
  public static ArrayList<R16List> clusterRhythmPartition(ArrayList<Rhythm> _partition) {
    if(_partition == null) throw new RuntimeException("clusterRhythmPartition:: partition is null.");
    ArrayList<R16List> partition = R16List.fromRhythmArray(_partition);
    if(partition.size()==1) {
      ArrayList<R16List> f = new ArrayList<>();
      f.add(partition.get(0));
      return f;
    }
    
    
    R16ListUnionSet us = new R16ListUnionSet();
    for(R16List r: partition) {us.add(r);}
    ArrayList<R16List> o = new ArrayList<R16List>();
    
    for(TreeSet<R16List> t : us.getTreeSets()) {
      R16List s = null;
      for(R16List l : t) {
        if(s==null) {s = (R16List)l.clone();}
        s = R16List.or(s, l);
      }
      o.add(s);
    }
    
    return CollectionUtils.reverse(o);
  }
  
  public Rhythm asRhythm(){
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
    return new Rhythm(b,n);
  }
  static public R16List parseR16Seq(String str) {
    String s = str.replace(" ", "");
    LinkedList<Rhythm16> output = new LinkedList<Rhythm16>();
    for (int i = 0; i < s.length() / 4; i++) {
      String tmp = s.substring(i * 4, (i + 1) * 4);
      tmp = tmp.substring(0, 2) + " " + tmp.substring(2, 4);
      output.add(Rhythm16.parseRhythm16Hex(tmp));
    }
    return new R16List(output);

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
  
  public static R16List rotate(R16List r, int t) {
    return fromRhythm(new Rhythm(r.asRhythm().rotate(t), r.size()*16));
  }
  
  public static R16List fromRhythm(Rhythm r){
    R16List output = new R16List();
    if(r.getN() % 16 != 0) {
      throw new RuntimeException("Rhythm's size is not divisible by 16.");
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
      output.add(Rhythm16.identifyRhythm16(t));
    }
    return output;
  }

  public static R16List not(R16List a) {
    R16List output = new R16List(a);

    for (int i = 0; i < output.size(); i++) {
      output.set(i, Rhythm16.not(output.get(i)));
    }
    return output;
  }

  public static R16List and(R16List a, R16List b) {
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

    R16List output = new R16List();
    for (int i = 0; i < n; i++) {
      output.add(Rhythm16.and(a.get(i), b.get(i)));
    }
    return output;
  }

  public static R16List or(R16List a, R16List b) {
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

    R16List output = new R16List();
    for (int i = 0; i < n; i++) {
      output.add(Rhythm16.or(a.get(i), b.get(i)));
    }
    return output;
  }

  public static R16List xor(R16List a, R16List b) {
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

    R16List output = new R16List();
    for (int i = 0; i < n; i++) {
      output.add(Rhythm16.xor(a.get(i), b.get(i)));
    }
    return output;
  }

  public static R16List minus(R16List a, R16List b) {
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

    R16List output = new R16List();
    for (int i = 0; i < n; i++) {
      output.add(Rhythm16.minus(a.get(i), b.get(i)));
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


  public static R16List convolve(R16List a, R16List b) {
    
    R16List carrier = a;
    R16List impulse = b;
    
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

    R16List output = new R16List();

    for (int i = 0; i < carrier.size(); i++) {
      TreeSet<Integer> t = new TreeSet<Integer>();

      for (int j = 0; j < 16; j++) {
        if (o[((i * 16) + j) % o.length]) {
          t.add(j);
        }
      }
      output.add(Rhythm16.identifyRhythm16(t));
    }
    return output;

  }

  public R16List decimate(){
    Sequence S = this.asRhythm().getComposition().segment().get(0).asSequence();
    
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
    List<Rhythm16> oo = new ArrayList<Rhythm16>();
    
    for(int i=0;i<rsz;i++) {
      oo.add(Rhythm16.identifyRhythm16(new Combination(b[i],16)));
    }
    return new R16List(oo);
  }
  public static R16List expand(R16List a, int x, boolean fill) {
    int n = x;
    Boolean[] b = R16List.toBooleanArray(a);
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
    R16List output = new R16List();

    for (int i = 0; i < o.length / 16; i++) {
      TreeSet<Integer> t = new TreeSet<Integer>();

      for (int j = 0; j < 16; j++) {
        if (o[(i * 16) + j]) {
          t.add(j);
        }
      }
      output.add(Rhythm16.identifyRhythm16(t));
    }
    return output;
  }

  public static Boolean[] toBooleanArray(R16List a) {

    Boolean output[] = new Boolean[a.size() * 16];
    for (int i = 0; i < a.size() * 16; i++) {
      output[i] = false;
    }

    for (int i = 0; i < a.size(); i++) {
      Rhythm16 x = a.get(i);

      for (int j = x.nextSetBit(0); j >= 0; j = x.nextSetBit(j + 1)) {
        output[j + (i * 16)] = true;
      }
    }
    return output;
  }

  public static R16List juxt(R16List a, R16List b) {
    R16List output = new R16List();

    for (int i = 0; i < a.size(); i++) {
      output.add(a.get(i));
    }
    for (int i = 0; i < b.size(); i++) {
      output.add(b.get(i));
    }
    return output;
  }

  public void append(R16List a) {
    for (int i = 0; i < a.size(); i++) {
      this.add(a.get(i));
    }
  }

  @Override
  public int compareTo(R16List o) {
    return o.toString().compareTo(this.toString());
  }

}
