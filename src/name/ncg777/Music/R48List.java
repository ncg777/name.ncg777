package name.ncg777.Music;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import name.ncg777.CS.DataStructures.CollectionUtils;
import name.ncg777.Maths.Objects.Combination;
import name.ncg777.Maths.Objects.Sequence;
// TODO: remove code repetition...
public class R48List extends LinkedList<Rhythm48> implements Comparable<R48List>{
  private static final long serialVersionUID = 1L;

  public R48List(List<Rhythm48> m_l) {
    super();
    for (Rhythm48 i : m_l) {
      this.add(i);
    }
  }

  public R48List() {
    super();
  }
  
  public boolean isEquivalentUnderSyncronizedRotation(R48List other) {
    if(other == null) return false;
    if(this.size() != other.size()) return false;
    
    for(int i=0;i<this.size();i++) {
      
      R48List rot = R48List.rotate(other, i*48);
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
  
  public static ArrayList<R48List> fromRhythmArray(ArrayList<Rhythm> list) {
    ArrayList<R48List> o = new ArrayList<>();
    
    for(Rhythm r : list) {
      o.add(R48List.fromRhythm(r));
    }
    return o;
  }
  
  public Sequence clusterPartition() {
    ArrayList<R48List> clusters = R48List.clusterRhythmPartition(this.asRhythm().partitionByEquality());
    ArrayList<Rhythm> rs= new ArrayList<>();
    for(R48List r : clusters) rs.add(r.asRhythm());
    
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
  
  private static class R48ListUnionSet {

    ArrayList<R48List> representants = new ArrayList<>();
    TreeMap<String,TreeSet<R48List>> instances = new TreeMap<>();
    
    public void add(R48List item) {
      boolean found = false;
      for(R48List r : representants) {
        if(r.isEquivalentUnderSyncronizedRotation(item)) {
          found=true;
          instances.get(r.toString()).add(item);
        }
      }
      if(!found) {
        TreeSet<R48List> inst = new TreeSet<>();
        inst.add(item);
        instances.put(item.toString(),inst);
        representants.add(item);
      }
    }
    public ArrayList<TreeSet<R48List>> getTreeSets() {
      ArrayList<TreeSet<R48List>> o = new ArrayList<>();
      o.addAll(instances.values());
      return o;
    }
  }
  
  public static ArrayList<R48List> clusterRhythmPartition(ArrayList<Rhythm> _partition) {
    if(_partition == null) throw new RuntimeException("clusterRhythmPartition:: partition is null.");
    ArrayList<R48List> partition = R48List.fromRhythmArray(_partition);
    if(partition.size()==1) {
      ArrayList<R48List> f = new ArrayList<>();
      f.add(partition.get(0));
      return f;
    }
    
    
    R48ListUnionSet us = new R48ListUnionSet();
    for(R48List r: partition) {us.add(r);}
    ArrayList<R48List> o = new ArrayList<R48List>();
    
    for(TreeSet<R48List> t : us.getTreeSets()) {
      R48List s = null;
      for(R48List l : t) {
        if(s==null) {s = (R48List)l.clone();}
        s = R48List.or(s, l);
      }
      o.add(s);
    }
    
    return CollectionUtils.reverse(o);
  }
  
  public Rhythm asRhythm(){
    int n = size()*48;
    BitSet b = new BitSet(n);
    for(int i=0;i<n;i++){
      int s = i / 48;
      try{
        b.set(i, this.get(s).get(i%48));
      }catch(Exception e){
        System.out.println("???");
      }
    }
    return new Rhythm(b,n);
  }
  static public R48List parseR48Seq(String str) {
    String s = str.replace(" ", "");
    LinkedList<Rhythm48> output = new LinkedList<Rhythm48>();
    for (int i = 0; i < s.length() / 12; i++) {
      String tmp = s.substring(i * 12, (i + 1) * 12);
      tmp = tmp.substring(0, 3) + " " + tmp.substring(3, 6) + " " + tmp.substring(6, 9)  + " " + tmp.substring(9, 12); 
      output.add(Rhythm48.parseRhythm48Tribbles(tmp));
    }
    return new R48List(output);

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
  
  public static R48List rotate(R48List r, int t) {
    return fromRhythm(new Rhythm(r.asRhythm().rotate(t), r.size()*48));
  }
  
  public static R48List fromRhythm(Rhythm r){
    R48List output = new R48List();
    if(r.getN() % 48 != 0) {
      throw new RuntimeException("Rhythm's size is not divisible by 48.");
    }
    int k = 0;
    while(k<r.getN()) {
      TreeSet<Integer> t = new TreeSet<Integer>();
      for(int i=0;i<48;i++) {
        if(r.get(k)) {
          t.add(k%48);
        }
        k++;
      }
      output.add(Rhythm48.identifyRhythm48(t));
    }
    return output;
  }

  public static R48List not(R48List a) {
    R48List output = new R48List(a);

    for (int i = 0; i < output.size(); i++) {
      output.set(i, Rhythm48.not(output.get(i)));
    }
    return output;
  }

  public static R48List and(R48List a, R48List b) {
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

    R48List output = new R48List();
    for (int i = 0; i < n; i++) {
      output.add(Rhythm48.and(a.get(i), b.get(i)));
    }
    return output;
  }

  public static R48List or(R48List a, R48List b) {
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

    R48List output = new R48List();
    for (int i = 0; i < n; i++) {
      output.add(Rhythm48.or(a.get(i), b.get(i)));
    }
    return output;
  }

  public static R48List xor(R48List a, R48List b) {
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

    R48List output = new R48List();
    for (int i = 0; i < n; i++) {
      output.add(Rhythm48.xor(a.get(i), b.get(i)));
    }
    return output;
  }

  public static R48List minus(R48List a, R48List b) {
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

    R48List output = new R48List();
    for (int i = 0; i < n; i++) {
      output.add(Rhythm48.minus(a.get(i), b.get(i)));
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


  public static R48List convolve(R48List a, R48List b) {
    
    R48List carrier = a;
    R48List impulse = b;
    
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

    R48List output = new R48List();

    for (int i = 0; i < carrier.size(); i++) {
      TreeSet<Integer> t = new TreeSet<Integer>();

      for (int j = 0; j < 48; j++) {
        if (o[((i * 48) + j) % o.length]) {
          t.add(j);
        }
      }
      output.add(Rhythm48.identifyRhythm48(t));
    }
    return output;

  }

  public R48List decimate(){
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
    for(int i=0;i<rsz*48;i++) {
      if(t==0) {
        try{
          t = S.get(k++);
        } catch(IndexOutOfBoundsException ex) {
          break;
        }
        keep = true;
      }
      int rd = i/48;
      int rr = i%48;
      
      if(this.get(rd).get(rr)) {
        t--;
        if(keep) {
          keep = false;
        } else {
          b[rd].set(rr,false);
        }
      }
    }
    List<Rhythm48> oo = new ArrayList<Rhythm48>();
    
    for(int i=0;i<rsz;i++) {
      oo.add(Rhythm48.identifyRhythm48(new Combination(b[i],48)));
    }
    return new R48List(oo);
  }
  public static R48List expand(R48List a, int x, boolean fill) {
    int n = x;
    Boolean[] b = R48List.toBooleanArray(a);
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
    R48List output = new R48List();

    for (int i = 0; i < o.length / 48; i++) {
      TreeSet<Integer> t = new TreeSet<Integer>();

      for (int j = 0; j < 48; j++) {
        if (o[(i * 48) + j]) {
          t.add(j);
        }
      }
      output.add(Rhythm48.identifyRhythm48(t));
    }
    return output;
  }

  public static Boolean[] toBooleanArray(R48List a) {

    Boolean output[] = new Boolean[a.size() * 48];
    for (int i = 0; i < a.size() * 48; i++) {
      output[i] = false;
    }

    for (int i = 0; i < a.size(); i++) {
      Rhythm48 x = a.get(i);

      for (int j = x.nextSetBit(0); j >= 0; j = x.nextSetBit(j + 1)) {
        output[j + (i * 48)] = true;
      }
    }
    return output;
  }

  public static R48List juxt(R48List a, R48List b) {
    R48List output = new R48List();

    for (int i = 0; i < a.size(); i++) {
      output.add(a.get(i));
    }
    for (int i = 0; i < b.size(); i++) {
      output.add(b.get(i));
    }
    return output;
  }

  public void append(R48List a) {
    for (int i = 0; i < a.size(); i++) {
      this.add(a.get(i));
    }
  }

  @Override
  public int compareTo(R48List o) {
    return o.toString().compareTo(this.toString());
  }

}
