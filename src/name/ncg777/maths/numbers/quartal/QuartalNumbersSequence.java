package name.ncg777.maths.numbers.quartal;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import name.ncg777.maths.Combination;
import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.Natural;
import name.ncg777.maths.sequences.Sequence;

public class QuartalNumbersSequence extends ArrayList<QuartalNumber> implements Comparable<QuartalNumbersSequence> {
  private static final long serialVersionUID = 1L;
  private Cipher.Name alphabetName;
  
  public QuartalNumbersSequence(Cipher.Name alphabetName) {
    this.alphabetName = alphabetName;
  }
  
  public QuartalNumbersSequence(Cipher.Name alphabetName, String string) {
    this(alphabetName);
    string = string.replaceAll("\\s+", "");

    if(string.length() % 4 != 0) throw new IllegalArgumentException();
    
    for(int i=(string.length()/4)-1;i>=0;i--) {
      this.add(new QuartalNumber(alphabetName, 
          (new StringBuilder(string.substring(i*4,(i+1)*4)).toString())));
    }
  }
  
  public QuartalNumbersSequence(Cipher.Name alphabetName, Natural natural) {
    this(alphabetName, natural.toString());
  }
  
  public QuartalNumbersSequence(Cipher.Name alphabetName, BinaryNatural binaryNatural) {
    this(alphabetName, binaryNatural.toNatural(alphabetName));
  }
  
  public Natural toNatural() {
    return new Natural(alphabetName,toString().replaceAll("\\s", ""));
  }
  
  public BinaryNatural toBinaryNatural() {
    return toNatural().toBinaryNatural();
  }
  
  @Override
  public String toString() {
    var sb = new StringBuilder();
    for(int i=this.size()-1;i>=0;i--) sb.append(this.get(i).toString(true) + " ");
    return sb.toString().trim();
  }
  
  public static QuartalNumbersSequence expand(QuartalNumbersSequence a, int x, boolean fill) {
    return expand(a,x,fill,null);
  }
  public static QuartalNumbersSequence expand(QuartalNumbersSequence a, int x, boolean fill, List<QuartalNumbersSequence> _patterns) {
    List<BinaryNatural> patterns = _patterns == null ? null : _patterns.stream().map(q -> q.toBinaryNatural().reverse()).toList();
    BinaryNatural b = a.toBinaryNatural().reverse();
    BinaryNatural o = new BinaryNatural(new BitSet(), x * b.getN());
    int k=0;
    for (int i = 0; i < b.getN(); i++) {
      if(b.get(i)) {
        if(!fill && patterns != null) {
          var pattern = patterns.get((k++)%patterns.size());
          for(int j=0;j<x;j++) {
            o.set(((i*x)+j)%o.size(), pattern.get(j%pattern.size()));
          }
        } else if(fill) {
          for(int j=0; j<x;j++) {
            o.set(((i * x) + j)%o.size());
          }
        } else {
          o.set(i*x);
        }
      }
    }
    
    QuartalNumbersSequence output = new QuartalNumbersSequence(a.alphabetName, o.reverse());
    
    return output;
  }
  
  public static QuartalNumbersSequence rotate(QuartalNumbersSequence r, int t) {
    return new QuartalNumbersSequence(r.alphabetName, BinaryNatural.build(r.toBinaryNatural().rotate(t)).reverse());
  }

  public static QuartalNumbersSequence not(QuartalNumbersSequence a) {
    return new QuartalNumbersSequence(a.alphabetName, a.toBinaryNatural().invert());
  }

  public static QuartalNumbersSequence and(QuartalNumbersSequence a, QuartalNumbersSequence b) {
    if(!a.alphabetName.equals(b.alphabetName))
      throw new IllegalArgumentException();
    
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

    QuartalNumbersSequence output = new QuartalNumbersSequence(a.alphabetName);
    for (int i = 0; i < n; i++) {
      output.add(
          new QuartalNumber(
              BinaryNatural.build(
                  a.get(i).toBinaryNatural().intersect(b.get(i).toBinaryNatural())
              ).reverse().toNatural(a.alphabetName)
          )
      );
    }
    return output;
  }

  public static QuartalNumbersSequence or(QuartalNumbersSequence a, QuartalNumbersSequence b) {
    if(!a.alphabetName.equals(b.alphabetName))
      throw new IllegalArgumentException();
    
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

    QuartalNumbersSequence output = new QuartalNumbersSequence(a.alphabetName);
    
    for (int i = 0; i < n; i++) {
      output.add(new QuartalNumber(
          BinaryNatural.build(
              Combination.merge(
                  a.get(i).toBinaryNatural(), 
                  b.get(i).toBinaryNatural())).reverse()
          .toNatural(a.alphabetName)));
    }
    return output;
  }

  public static QuartalNumbersSequence xor(QuartalNumbersSequence a, QuartalNumbersSequence b) {
    if(!a.alphabetName.equals(b.alphabetName))
      throw new IllegalArgumentException();
    
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

    QuartalNumbersSequence output = new QuartalNumbersSequence(a.alphabetName);
    for (int i = 0; i < n; i++) {
      output.add(
          new QuartalNumber(
              BinaryNatural.build(
                  (a.get(i).toBinaryNatural().symmetricDifference(b.get(i).toBinaryNatural()))).reverse().toNatural(a.alphabetName)
          )
      );
    }
    return output;
  }

  public static QuartalNumbersSequence minus(QuartalNumbersSequence a, QuartalNumbersSequence b) {
    if(!a.alphabetName.equals(b.alphabetName))
      throw new IllegalArgumentException();
    
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

    QuartalNumbersSequence output = new QuartalNumbersSequence(a.alphabetName);
    for (int i = 0; i < n; i++) {
      output.add(
          new QuartalNumber(
              BinaryNatural.build(
                  (a.get(i).toBinaryNatural().minus(b.get(i).toBinaryNatural()))
              ).reverse().toNatural(a.alphabetName)
          )
      );
    }
    return output;
  }

  public static QuartalNumbersSequence convolve(QuartalNumbersSequence a, QuartalNumbersSequence b) {
    if(!a.alphabetName.equals(b.alphabetName))
      throw new IllegalArgumentException();
    
    QuartalNumbersSequence carrier = a;
    QuartalNumbersSequence impulse = b;

    BinaryNatural b_carrier = carrier.toBinaryNatural().reverse();
    BinaryNatural b_impulse = impulse.toBinaryNatural().reverse();

    BinaryNatural o = new BinaryNatural(new BitSet(), b_carrier.getN());

    for (int i = 0; i < b_carrier.getN(); i++) {
      for (int j = 0; j < b_impulse.getN(); j++) {
        o.set(
            (i + j) % o.getN(), 
            o.get((i + j) % o.getN()) | (b_carrier.get(i) & b_impulse.get(j))
        );
      }
    }
    
    return new QuartalNumbersSequence(a.alphabetName, o.reverse());
  }

  public boolean isEquivalentUnderSyncronizedRotation(QuartalNumbersSequence other) {
    if(!this.alphabetName.equals(other.alphabetName))
      throw new IllegalArgumentException();
    var abc = Cipher.getAlphabet(this.alphabetName);
    
    if (this.size() != other.size()) return false;

    for (int i = 0; i < this.size(); i++) {
      QuartalNumbersSequence rot = QuartalNumbersSequence.rotate(other, i * (4*((int)Math.round(abc.information()))));
      boolean eq = true;
      for (int j = 0; j < rot.size(); j++) {
        if (!this.get(j).toString().equals(rot.get(j).toString())) {
          eq = false;
          continue;
        }
      }
      if (eq) return true;
    }
    return false;
  }

  public Sequence clusterPartition(Cipher.Name alphabetName) {
    var clusters =
        QuartalNumbersSequence.clusterRhythmPartition(alphabetName, this.toBinaryNatural().decomposeIntoHomogeneousRegions());
    var rs = new ArrayList<BinaryNatural>();
    for (QuartalNumbersSequence r : clusters)
      rs.add(r.toBinaryNatural());

    Sequence o = new Sequence();

    int n = rs.get(0).getN();

    for (int i = 0; i < n; i++) {
      for (int j = 0; j < clusters.size(); j++) {
        if (rs.get(j).get(i)) {
          o.add(j);
        }
      }
    }
    return o.reverse();
  }

  private static class QuartalNumbersSequenceUnionSet {
    ArrayList<QuartalNumbersSequence> representants = new ArrayList<>();
    TreeMap<String, TreeSet<QuartalNumbersSequence>> instances = new TreeMap<>();

    public void add(QuartalNumbersSequence item) {
      boolean found = false;
      for (QuartalNumbersSequence r : representants) {
        if (r.isEquivalentUnderSyncronizedRotation(item)) {
          found = true;
          instances.get(r.toString()).add(item);
        }
      }
      if (!found) {
        TreeSet<QuartalNumbersSequence> inst = new TreeSet<>();
        inst.add(item);
        instances.put(item.toString(), inst);
        representants.add(item);
      }
    }

    public ArrayList<TreeSet<QuartalNumbersSequence>> getTreeSets() {
      ArrayList<TreeSet<QuartalNumbersSequence>> o = new ArrayList<>();
      o.addAll(instances.values());
      return o;
    }
  }
  public static QuartalNumbersSequence fromCombination(Cipher.Name alphabetName, Combination r){
    QuartalNumbersSequence output = new QuartalNumbersSequence(alphabetName);
    var abc = Cipher.getAlphabet(alphabetName);
    
    if(!abc.isInformationBinary())
      throw new IllegalArgumentException();
    
    var sz_t = (((int)Math.round(abc.information()))*4);
    if(r.getN() % sz_t != 0) {
      throw new RuntimeException("Rhythm's size is not divisible by " + Integer.toString(sz_t));
    }
    
    int k = 0;
    
    while(k<r.getN()) {
      var t = new BinaryNatural(new BitSet(),sz_t);
      for(int i=0;i<sz_t;i++) {
        if(r.get(k)) {
          t.set(k%sz_t, true);
        }
        k++;
      }
      output.add(new QuartalNumber(t.toNatural(alphabetName)));
    }
    return output;
  }
  
  public static List<QuartalNumbersSequence> fromRhythmList(
      Cipher.Name alphabetName, List<? extends Combination> list) {
    List<QuartalNumbersSequence> o = new ArrayList<>();
    
    for(var r : list) {
      o.add(QuartalNumbersSequence.fromCombination(alphabetName,r));
    }
    return o;
  }
  
  public static List<QuartalNumbersSequence> clusterRhythmPartition(
      Cipher.Name alphabetName, List<? extends Combination> _partition) {
    if(_partition == null) throw new RuntimeException("clusterRhythmPartition:: partition is null.");
    List<QuartalNumbersSequence> partition = QuartalNumbersSequence.fromRhythmList(alphabetName, _partition);
    if(partition.size()==1) {
      ArrayList<QuartalNumbersSequence> f = new ArrayList<>();
      f.add(partition.get(0));
      return f;
    }
    
    QuartalNumbersSequenceUnionSet us = new QuartalNumbersSequenceUnionSet();
    for(QuartalNumbersSequence r: partition) {us.add(r);}
    ArrayList<QuartalNumbersSequence> o = new ArrayList<QuartalNumbersSequence>();
    
    for(TreeSet<QuartalNumbersSequence> t : us.getTreeSets()) {
      QuartalNumbersSequence s = null;
      for(QuartalNumbersSequence l : t) {
        if(s==null) {s = (QuartalNumbersSequence)l.clone();}
        s = QuartalNumbersSequence.or(s, l);
      }
      o.add(s);
    }
    
    return o.reversed();
  }

  @Override
  public int compareTo(QuartalNumbersSequence o) {
    return this.toString().compareTo(o.toString());
  }

}
