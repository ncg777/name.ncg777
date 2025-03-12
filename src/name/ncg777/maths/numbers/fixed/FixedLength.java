package name.ncg777.maths.numbers.fixed;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;


import name.ncg777.maths.Combination;
import name.ncg777.maths.enumerations.FixedLengthNaturalEnumeration;
import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.sequences.Sequence;

public class FixedLength {
  
  protected int L = -1;
  public FixedLength(int L) {this.L=L;}
  public static FixedLength of(int L) {return new FixedLength(L);}
  public Class<? extends Natural> getNatural() {return Natural.class;}
  public Class<? extends NaturalSequence> getNaturalSequence() {return NaturalSequence.class;}
  
  public static Natural newNatural(int L, Cipher.Name alphabetName, Character[] array) {
    return of(L).newNatural(alphabetName,array);
  }
  
  public static Natural newNatural(int L, Cipher.Name alphabetName, String string) {
    return of(L).newNatural(alphabetName,string);
  }
  
  public static Natural newNatural(int L, Cipher.Name alphabetName, List<Character> list) {
    return of(L).newNatural(alphabetName,list);
  }
  
  public static Natural newNatural(int L, Cipher.Name alphabetName, Sequence sequence) {
    return of(L).newNatural(alphabetName,sequence);
  }
 
  public static Natural newNatural(int L, Natural natural) {
    return of(L).newNatural(natural);
  }
  public static Natural newNatural(int L, BinaryNatural natural) {
    return of(L).newNatural(natural);
  }
  
  public static NaturalSequence newNaturalSequence(int L, Cipher.Name alphabetName) {
    return of(L).newNaturalSequence(alphabetName);
  }
  public static NaturalSequence newNaturalSequence(int L, NaturalSequence seq) {
    return of(L).newNaturalSequence(seq);
  }
  public static NaturalSequence newNaturalSequence(int L, Cipher.Name alphabetName, String string) {
    return of(L).newNaturalSequence(alphabetName,string);
  }
  
  public static NaturalSequence newNaturalSequence(int L, Cipher.Name alphabetName, name.ncg777.maths.numbers.Natural natural) {
    return of(L).newNaturalSequence(alphabetName, natural);
  }
  
  public static NaturalSequence newNaturalSequence(int L, Cipher.Name alphabetName, BinaryNatural binaryNatural) {
    return of(L).newNaturalSequence(alphabetName,binaryNatural);
  }
  
  public Natural newNatural(Cipher.Name alphabetName, Character[] array) {
    return new Natural(alphabetName,array);
  }
  
  public Natural newNatural(Cipher.Name alphabetName, String string) {
    return new Natural(alphabetName,string);
  }
  
  public Natural newNatural(Cipher.Name alphabetName, List<Character> list) {
    return new Natural(alphabetName,list);
  }
  
  public Natural newNatural(Cipher.Name alphabetName, Sequence sequence) {
    return new Natural(alphabetName,sequence);
  }
 
  public Natural newNatural(Natural natural) {
    return new Natural(natural);
  }
  public Natural newNatural(name.ncg777.maths.numbers.Natural natural) {
    return new Natural(natural);
  }
  public Natural newNatural(BinaryNatural natural) {
    return new Natural(natural);
  }
  
  public NaturalSequence newNaturalSequence(Cipher.Name alphabetName) {
    return new NaturalSequence(alphabetName);
  }
  public NaturalSequence newNaturalSequence(NaturalSequence seq) {
    return new NaturalSequence(seq);
  }
  public NaturalSequence newNaturalSequence(Cipher.Name alphabetName, String string) {
    return new NaturalSequence(alphabetName,string);
  }
  
  public NaturalSequence newNaturalSequence(Cipher.Name alphabetName, name.ncg777.maths.numbers.Natural natural) {
    return new NaturalSequence(alphabetName,natural);
  }
  
  public NaturalSequence newNaturalSequence(Cipher.Name alphabetName, BinaryNatural binaryNatural) {
    return new NaturalSequence(alphabetName,binaryNatural);
  }
  
  public static TreeSet<FixedLength.Natural> generate(int L, Cipher.Name alphabetName) {
    TreeSet<FixedLength.Natural> o = new TreeSet<FixedLength.Natural>();
    var tge = new FixedLengthNaturalEnumeration(L, alphabetName);
    while(tge.hasMoreElements()) o.add(tge.nextElement());
    return o;
  }
  
  public class Natural extends name.ncg777.maths.numbers.Natural {
    public int getL() {return L;}

    public FixedLength getFixedLength() {return FixedLength.this;}
    private static final long serialVersionUID = 1L;
    public Natural(name.ncg777.maths.numbers.Natural nat) {
      super(nat);
      if(nat.size()!=getL()) throw new IllegalArgumentException();
    }
    
    public Natural(Cipher.Name alphabetName, Character[] array) {
      super(alphabetName, array);
    }
    
    public Natural(Cipher.Name alphabetName, String string) {
      super(alphabetName, string.replaceAll("\\s+", ""));
      if(string.replaceAll("\\s+", "").length() != L) throw new IllegalArgumentException();
    }
    
    public Natural(Cipher.Name alphabetName, List<Character> list) {
      super(alphabetName, list);
    }
    
    public Natural(Cipher.Name alphabetName, Sequence sequence) {
      super(alphabetName, sequence);
      if(sequence.size()!= L) throw new IllegalArgumentException();
    }
   
    public Natural(Natural natural) {
      super(natural);
      if(natural.size()!= L) throw new IllegalArgumentException();
    }
      
    public Natural(BinaryNatural binaryNatural) {
      super(binaryNatural.toNatural(name.ncg777.maths.numbers.Cipher.Name.Hexadecimal));
    }
    
    @Override
    public String toString() {
      return this.toString(false);
    }
    
    public String toString(boolean insertSpace) {
      StringBuilder sb = new StringBuilder();
      int i=L;
      while(--i>=0) {
        if(insertSpace && i%2==1) sb.append(" ");
        sb.append(this.get(i));
      }
      
      return sb.toString().trim();
    }
    
    public int compareTo(FixedLength.Natural o) {
      if(o.getL()!=this.getL()) {
        return this.getL()-o.getL();
      }
      return this.toSequence().compareTo(o.toSequence());
    }
  }
  
  
  public class NaturalSequence extends ArrayList<FixedLength.Natural> implements Comparable<FixedLength.NaturalSequence> {
    private static final long serialVersionUID = 1L;
    private Cipher.Name alphabetName;
    public int getL() {return L;}
    public FixedLength getFixedLength() {return FixedLength.this;}
    public NaturalSequence(Cipher.Name alphabetName) {
      this.alphabetName = alphabetName;
    }
    public NaturalSequence(NaturalSequence seq) {
      super(seq);
      this.alphabetName = seq.alphabetName;
    }
    public NaturalSequence(Cipher.Name alphabetName, String string) {
      this(alphabetName);
      string = string.replaceAll("\\s+", "");

      if(string.length() % L != 0) throw new IllegalArgumentException();
      
      for(int i=(string.length()/L)-1;i>=0;i--) {
        this.add(new FixedLength.Natural(alphabetName, 
            (new StringBuilder(string.substring(i*L,(i+1)*L)).toString())));
      }
    }
    
    public NaturalSequence(Cipher.Name alphabetName, name.ncg777.maths.numbers.Natural natural) {
      this(alphabetName, natural.toString());
    }
    
    public NaturalSequence(Cipher.Name alphabetName, BinaryNatural binaryNatural) {
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
    
    public static NaturalSequence expand(NaturalSequence a, int x, boolean fill) {
      return expand(a,x,fill,null);
    }
    public static NaturalSequence expand(NaturalSequence a, int x, boolean fill, List<? extends NaturalSequence> _patterns) {
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
      
      NaturalSequence output = of(a.getL()).newNaturalSequence(a.alphabetName, o.reverse());
      
      return output;
    }
    
    public static NaturalSequence rotate(NaturalSequence r, int t) {
      return of(r.getL()).newNaturalSequence(r.alphabetName, BinaryNatural.build(r.toBinaryNatural().rotate(t)).reverse());
    }

    public static NaturalSequence not(NaturalSequence a) {
      return of(a.getL()).newNaturalSequence(a.alphabetName, a.toBinaryNatural().invert());
    }

    public static NaturalSequence and(NaturalSequence a, NaturalSequence b) {
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

      NaturalSequence output = of(a.getL()).newNaturalSequence(a.alphabetName);
     
      for (int i = 0; i < n; i++) {
        output.add(
            of(a.getL()).newNatural(
                BinaryNatural.build(
                    a.get(i).toBinaryNatural().intersect(b.get(i).toBinaryNatural())
                ).reverse().toNatural(a.alphabetName)
            )
        );
      }
      return output;
    }

    public static NaturalSequence or(NaturalSequence a, NaturalSequence b) {
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

      NaturalSequence output = of(a.getL()).newNaturalSequence(a.alphabetName);
      
      
      for (int i = 0; i < n; i++) {
        output.add(of(a.getL()).newNatural(
            BinaryNatural.build(
                Combination.merge(
                    a.get(i).toBinaryNatural(), 
                    b.get(i).toBinaryNatural())).reverse()
            .toNatural(a.alphabetName)));
      }
      return output;
    }

    public static NaturalSequence xor(NaturalSequence a, NaturalSequence b) {
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

      NaturalSequence output = of(a.getL()).newNaturalSequence(a.alphabetName);
     
      for (int i = 0; i < n; i++) {
        output.add(of(a.getL()).newNatural(
            BinaryNatural.build(
                (a.get(i).toBinaryNatural()
                    .symmetricDifference(b.get(i).toBinaryNatural())))
            .reverse().toNatural(a.alphabetName)
            )
            );
      }
    
      return output;
    }

    public static NaturalSequence minus(NaturalSequence a, NaturalSequence b) {
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

      NaturalSequence output = of(a.getL()).newNaturalSequence(a.alphabetName);
      
      for (int i = 0; i < n; i++) {
        output.add(
            of(a.getL()).newNatural(
                BinaryNatural.build(
                    (a.get(i).toBinaryNatural().minus(b.get(i).toBinaryNatural()))
                ).reverse().toNatural(a.alphabetName)
            )
        );
      }
      return output;
    }

    public static NaturalSequence convolve(NaturalSequence a, NaturalSequence b) {
      if(!a.alphabetName.equals(b.alphabetName))
        throw new IllegalArgumentException();
      
      NaturalSequence carrier = a;
      NaturalSequence impulse = b;

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
      return of(a.getL()).newNaturalSequence(a.alphabetName, o.reverse());
    }

    public boolean isEquivalentUnderSyncronizedRotation(NaturalSequence other) {
      if(!this.alphabetName.equals(other.alphabetName))
        throw new IllegalArgumentException();
      var abc = Cipher.getCipher(this.alphabetName);
      
      if (this.size() != other.size()) return false;

      for (int i = 0; i < this.size(); i++) {
        NaturalSequence rot = NaturalSequence.rotate(other, i * (4*((int)Math.round(abc.information()))));
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

    public Sequence clusterPartition(int L,Cipher.Name alphabetName) {
      var clusters =
          NaturalSequence.clusterRhythmPartition(L,alphabetName, this.toBinaryNatural().decomposeIntoHomogeneousRegions());
      var rs = new ArrayList<BinaryNatural>();
      for (NaturalSequence r : clusters)
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

    private static class FixedLengthNaturalSequencesUnionSet {
      ArrayList<NaturalSequence> representants = new ArrayList<>();
      TreeMap<String, TreeSet<NaturalSequence>> instances = new TreeMap<>();

      public void add(NaturalSequence item) {
        boolean found = false;
        for (NaturalSequence r : representants) {
          if (r.isEquivalentUnderSyncronizedRotation(item)) {
            found = true;
            instances.get(r.toString()).add(item);
          }
        }
        if (!found) {
          TreeSet<NaturalSequence> inst = new TreeSet<>();
          inst.add(item);
          instances.put(item.toString(), inst);
          representants.add(item);
        }
      }

      public ArrayList<TreeSet<NaturalSequence>> getTreeSets() {
        ArrayList<TreeSet<NaturalSequence>> o = new ArrayList<>();
        o.addAll(instances.values());
        return o;
      }
    }
    public static NaturalSequence fromCombination(int L, Cipher.Name alphabetName, Combination r){
      var fl = new FixedLength(L);
      NaturalSequence output;
      try {
        output = fl.newNaturalSequence(alphabetName);
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
      var abc = Cipher.getCipher(alphabetName);
      
      if(!abc.isInformationBinary())
        throw new IllegalArgumentException();
      
      var sz_t = (((int)Math.round(abc.information()))*L);
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
        output.add(of(L).newNatural(t.toNatural(alphabetName)));
      }
      return output;
    }
    
    public static List<NaturalSequence> fromCombinationList(int L,
        Cipher.Name alphabetName, List<? extends Combination> list) {
      List<NaturalSequence> o = new ArrayList<>();
      
      for(var r : list) {
        o.add(NaturalSequence.fromCombination(L,alphabetName,r));
      }
      return o;
    }
    
    public static List<NaturalSequence> clusterRhythmPartition(int L,
        Cipher.Name alphabetName, List<? extends Combination> _partition) {
      if(_partition == null) throw new RuntimeException("clusterRhythmPartition:: partition is null.");
      List<NaturalSequence> partition = NaturalSequence.fromCombinationList(L,alphabetName, _partition);
      if(partition.size()==1) {
        ArrayList<NaturalSequence> f = new ArrayList<>();
        f.add(partition.get(0));
        return f;
      }
      
      FixedLengthNaturalSequencesUnionSet us = new FixedLengthNaturalSequencesUnionSet();
      for(NaturalSequence r: partition) {us.add(r);}
      ArrayList<NaturalSequence> o = new ArrayList<NaturalSequence>();
      
      for(TreeSet<NaturalSequence> t : us.getTreeSets()) {
        NaturalSequence s = null;
        for(NaturalSequence l : t) {
          if(s==null) {s = (NaturalSequence)l.clone();}
          s = NaturalSequence.or(s, l);
        }
        o.add(s);
      }
      
      return o.reversed();
    }

    @Override
    public int compareTo(FixedLength.NaturalSequence o) {
      return this.toString().compareTo(o.toString());
    }

  }
  
}
