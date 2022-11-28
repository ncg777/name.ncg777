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

public class R12List extends LinkedList<Rhythm12>  implements Comparable<R12List>{
  private static final long serialVersionUID = 1L;

  public R12List(List<Rhythm12> m_l) {
    super();
    for (Rhythm12 i : m_l) {
      this.add(i);
    }
  }

  public R12List() {
    super();
  }
  
  public Rhythm asRhythm(){
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
    return new Rhythm(b,n);
  }
  static public R12List parseR12Seq(String str) {
    String s = str.replace(" ", "");
    LinkedList<Rhythm12> output = new LinkedList<Rhythm12>();
    for (int i = 0; i < s.length() / 4; i++) {
      String tmp = s.substring(i * 4, (i + 1) * 4);
      tmp = tmp.substring(0, 2) + " " + tmp.substring(2, 4);
      output.add(Rhythm12.parseRhythm12Octal(tmp));
    }
    return new R12List(output);

  }

  public boolean isEquivalentUnderSyncronizedRotation(R12List other) {
    if(other == null) return false;
    if(this.size() != other.size()) return false;
    
    for(int i=0;i<this.size();i++) {
      
      R12List rot = R12List.rotate(other, i*12);
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
  
  public static ArrayList<R12List> fromRhythmArray(ArrayList<Rhythm> list) {
    ArrayList<R12List> o = new ArrayList<>();
    
    for(Rhythm r : list) {
      o.add(R12List.fromRhythm(r));
    }
    return o;
  }
  
  public Sequence clusterPartition() {
    ArrayList<R12List> clusters = R12List.clusterRhythmPartition(this.asRhythm().partitionByEquality());
    ArrayList<Rhythm> rs= new ArrayList<>();
    for(R12List r : clusters) rs.add(r.asRhythm());
    
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

    ArrayList<R12List> representants = new ArrayList<>();
    TreeMap<String,TreeSet<R12List>> instances = new TreeMap<>();
    
    public void add(R12List item) {
      boolean found = false;
      for(R12List r : representants) {
        if(r.isEquivalentUnderSyncronizedRotation(item)) {
          found=true;
          instances.get(r.toString()).add(item);
        }
      }
      if(!found) {
        TreeSet<R12List> inst = new TreeSet<>();
        inst.add(item);
        instances.put(item.toString(),inst);
        representants.add(item);
      }
    }
    public ArrayList<TreeSet<R12List>> getTreeSets() {
      ArrayList<TreeSet<R12List>> o = new ArrayList<>();
      o.addAll(instances.values());
      return o;
    }
  }
  
  public static ArrayList<R12List> clusterRhythmPartition(ArrayList<Rhythm> _partition) {
    if(_partition == null) throw new RuntimeException("clusterRhythmPartition:: partition is null.");
    ArrayList<R12List> partition = R12List.fromRhythmArray(_partition);
    if(partition.size()==1) {
      ArrayList<R12List> f = new ArrayList<>();
      f.add(partition.get(0));
      return f;
    }
    
    
    R12ListUnionSet us = new R12List.R12ListUnionSet();
    for(R12List r: partition) {us.add(r);}
    ArrayList<R12List> o = new ArrayList<R12List>();
    
    for(TreeSet<R12List> t : us.getTreeSets()) {
      R12List s = null;
      for(R12List l : t) {
        if(s==null) {s = (R12List)l.clone();}
        s = R12List.or(s, l);
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
  
  public static R12List rotate(R12List r, int t) {
    return fromRhythm(new Rhythm(r.asRhythm().rotate(t), r.size()*12));
  }
  
  public static R12List fromRhythm(Rhythm r){
    R12List output = new R12List();
    if(r.getN() % 12 != 0) {
      throw new RuntimeException("Rhythm's size is not divisible by 12.");
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
      output.add(Rhythm12.identifyRhythm12(t));
    }
    return output;
  }

  public static R12List not(R12List a) {
    R12List output = new R12List(a);

    for (int i = 0; i < output.size(); i++) {
      output.set(i, Rhythm12.not(output.get(i)));
    }
    return output;
  }

  public static R12List and(R12List a, R12List b) {
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

    R12List output = new R12List();
    for (int i = 0; i < n; i++) {
      output.add(Rhythm12.and(a.get(i), b.get(i)));
    }
    return output;
  }

  public static R12List or(R12List a, R12List b) {
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

    R12List output = new R12List();
    for (int i = 0; i < n; i++) {
      output.add(Rhythm12.or(a.get(i), b.get(i)));
    }
    return output;
  }

  public static R12List xor(R12List a, R12List b) {
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

    R12List output = new R12List();
    for (int i = 0; i < n; i++) {
      output.add(Rhythm12.xor(a.get(i), b.get(i)));
    }
    return output;
  }

  public static R12List minus(R12List a, R12List b) {
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

    R12List output = new R12List();
    for (int i = 0; i < n; i++) {
      output.add(Rhythm12.minus(a.get(i), b.get(i)));
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


  public static R12List convolve(R12List a, R12List b) {
    
    R12List carrier = a;
    R12List impulse = b;
    
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

    R12List output = new R12List();

    for (int i = 0; i < carrier.size(); i++) {
      TreeSet<Integer> t = new TreeSet<Integer>();

      for (int j = 0; j < 12; j++) {
        if (o[((i * 12) + j) % o.length]) {
          t.add(j);
        }
      }
      output.add(Rhythm12.identifyRhythm12(t));
    }
    return output;

  }

  public R12List decimate(){
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
    List<Rhythm12> oo = new ArrayList<Rhythm12>();
    
    for(int i=0;i<rsz;i++) {
      oo.add(Rhythm12.identifyRhythm12(new Combination(b[i],12)));
    }
    return new R12List(oo);
  }
  public static R12List expand(R12List a, int x, boolean fill) {
    int n = x;
    Boolean[] b = R12List.toBooleanArray(a);
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
    R12List output = new R12List();

    for (int i = 0; i < o.length / 12; i++) {
      TreeSet<Integer> t = new TreeSet<Integer>();

      for (int j = 0; j < 12; j++) {
        if (o[(i * 12) + j]) {
          t.add(j);
        }
      }
      output.add(Rhythm12.identifyRhythm12(t));
    }
    return output;
  }

  public static Boolean[] toBooleanArray(R12List a) {

    Boolean output[] = new Boolean[a.size() * 12];
    for (int i = 0; i < a.size() * 12; i++) {
      output[i] = false;
    }

    for (int i = 0; i < a.size(); i++) {
      Rhythm12 x = a.get(i);

      for (int j = x.nextSetBit(0); j >= 0; j = x.nextSetBit(j + 1)) {
        output[j + (i * 12)] = true;
      }
    }
    return output;
  }

  public static R12List juxt(R12List a, R12List b) {
    R12List output = new R12List();

    for (int i = 0; i < a.size(); i++) {
      output.add(a.get(i));
    }
    for (int i = 0; i < b.size(); i++) {
      output.add(b.get(i));
    }
    return output;
  }

  public void append(R12List a) {
    for (int i = 0; i < a.size(); i++) {
      this.add(a.get(i));
    }
  }

  @Override
  public int compareTo(R12List o) {
    return o.toString().compareTo(this.toString());
  }
}
