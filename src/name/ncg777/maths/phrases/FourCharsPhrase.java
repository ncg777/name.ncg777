package name.ncg777.maths.phrases;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.TreeMap;
import java.util.TreeSet;

import name.ncg777.computing.structures.CollectionUtils;
import name.ncg777.maths.Combination;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.words.Alphabet;
import name.ncg777.maths.words.BinaryWord;
import name.ncg777.maths.words.FourChars;
import name.ncg777.maths.words.Word;

public class FourCharsPhrase extends ArrayList<FourChars> {
  private static final long serialVersionUID = 1L;
  private Alphabet.Name alphabetName;
  
  public FourCharsPhrase(Alphabet.Name alphabetName) {
    this.alphabetName = alphabetName;
  }
  
  public FourCharsPhrase(Alphabet.Name alphabetName, String string) {
    this(alphabetName);
    string = string.replaceAll("\\s+", "");

    if(string.length() % 4 != 0) throw new IllegalArgumentException();
    
    for(int i=(string.length()/4)-1;i>=0;i--) {
      this.add(new FourChars(alphabetName, 
          (new StringBuilder(string.substring(i*4,(i+1)*4)).toString())));
    }
  }
  
  public FourCharsPhrase(Alphabet.Name alphabetName, Word word) {
    this(alphabetName, word.toString());
  }
  
  public FourCharsPhrase(Alphabet.Name alphabetName, BinaryWord binaryWord) {
    this(alphabetName, binaryWord.toWord(alphabetName));
  }
  
  public Word toWord() {
    return new Word(alphabetName,toString().replaceAll("\\s", ""));
  }
  
  public BinaryWord toBinaryWord() {
    return toWord().toBinaryWord();
  }
  
  @Override
  public String toString() {
    var sb = new StringBuilder();
    for(int i=this.size()-1;i>=0;i--) sb.append(this.get(i).toString(true) + " ");
    return sb.toString().trim();
  }
  
  public static FourCharsPhrase expand(FourCharsPhrase a, int x, boolean fill) {
    BinaryWord b = a.toBinaryWord();
    BinaryWord o = new BinaryWord(new BitSet(), x * b.getN());
    
    for (int i = 0; i < b.getN(); i++) {
      if(b.get(-1 + b.getN() - i)) {
        o.set(-1+ o.getN() - i*x);
        if(fill) {
          for(int j=1; j<x;j++) {
            o.set(-1 + o.getN() -((i * x) + j));
          }
        } 
      }
    }
    
    FourCharsPhrase output = new FourCharsPhrase(a.alphabetName, o);
    
    return output;
  }
  
  public static FourCharsPhrase rotate(FourCharsPhrase r, int t) {
    return new FourCharsPhrase(r.alphabetName, BinaryWord.build(r.toBinaryWord().rotate(t)));
  }

  public static FourCharsPhrase not(FourCharsPhrase a) {
    return new FourCharsPhrase(a.alphabetName, a.toBinaryWord().invert());
  }

  public static FourCharsPhrase and(FourCharsPhrase a, FourCharsPhrase b) {
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

    FourCharsPhrase output = new FourCharsPhrase(a.alphabetName);
    for (int i = 0; i < n; i++) {
      output.add(
          new FourChars(
              BinaryWord.build(
                  a.get(i).toBinaryWord().intersect(b.get(i).toBinaryWord())
              ).toWord(a.alphabetName)
          )
      );
    }
    return output;
  }

  public static FourCharsPhrase or(FourCharsPhrase a, FourCharsPhrase b) {
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

    FourCharsPhrase output = new FourCharsPhrase(a.alphabetName);
    
    for (int i = 0; i < n; i++) {
      output.add(new FourChars(
          BinaryWord.build(
              Combination.merge(
                  a.get(i).toBinaryWord(), 
                  b.get(i).toBinaryWord()))
          .toWord(a.alphabetName)));
    }
    return output;
  }

  public static FourCharsPhrase xor(FourCharsPhrase a, FourCharsPhrase b) {
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

    FourCharsPhrase output = new FourCharsPhrase(a.alphabetName);
    for (int i = 0; i < n; i++) {
      output.add(
          new FourChars(
              BinaryWord.build(
                  (a.get(i).toBinaryWord().symmetricDifference(b.get(i).toBinaryWord()))
              ).toWord(a.alphabetName)
          )
      );
    }
    return output;
  }

  public static FourCharsPhrase minus(FourCharsPhrase a, FourCharsPhrase b) {
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

    FourCharsPhrase output = new FourCharsPhrase(a.alphabetName);
    for (int i = 0; i < n; i++) {
      output.add(
          new FourChars(
              BinaryWord.build(
                  (a.get(i).toBinaryWord().minus(b.get(i).toBinaryWord()))
              ).toWord(a.alphabetName)
          )
      );
    }
    return output;
  }

  public static FourCharsPhrase convolve(FourCharsPhrase a, FourCharsPhrase b) {
    if(!a.alphabetName.equals(b.alphabetName))
      throw new IllegalArgumentException();
    
    FourCharsPhrase carrier = a;
    FourCharsPhrase impulse = b;

    BinaryWord b_carrier = carrier.toBinaryWord();
    BinaryWord b_impulse = impulse.toBinaryWord();

    BinaryWord o = new BinaryWord(new BitSet(), b_carrier.size());

    for (int i = 0; i < b_carrier.size(); i++) {
      for (int j = 0; j < b_impulse.size(); j++) {
        o.set(
            (i + j) % o.size(), 
            o.get((i + j) % o.size()) | (b_carrier.get(i) & b_impulse.get(j))
        );
      }
    }

    BinaryWord output = new BinaryWord(new BitSet(), o.size());

    for (int i = 0; i < carrier.size(); i++) {
      for (int j = 0; j < 12; j++) {
        if (o.get(((i * 2) + j) % o.size())) {
          output.set(j,true);
        }
      }
    }
    
    return new FourCharsPhrase(a.alphabetName, output);
  }

  public boolean isEquivalentUnderSyncronizedRotation(FourCharsPhrase other) {
    if(this.alphabetName.equals(other.alphabetName))
      throw new IllegalArgumentException();
    var abc = Alphabet.getAlphabet(this.alphabetName);
    
    if (this.size() != other.size()) return false;

    for (int i = 0; i < this.size(); i++) {
      FourCharsPhrase rot = FourCharsPhrase.rotate(other, i * (4*((int)Math.round(abc.information()))));
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

  public Sequence clusterPartition(Alphabet.Name alphabetName) {
    var clusters =
        FourCharsPhrase.clusterRhythmPartition(alphabetName, this.toBinaryWord().partitionByEquality());
    ArrayList<BinaryWord> rs = new ArrayList<>();
    for (FourCharsPhrase r : clusters)
      rs.add(r.toBinaryWord());

    Sequence o = new Sequence();

    int n = rs.get(0).getN();

    for (int i = 0; i < n; i++) {
      for (int j = 0; j < clusters.size(); j++) {
        if (rs.get(j).get(i)) {
          o.add(j);
        }
      }
    }
    return o;
  }

  private static class FourCharsentenceUnionSet {
    ArrayList<FourCharsPhrase> representants = new ArrayList<>();
    TreeMap<String, TreeSet<FourCharsPhrase>> instances = new TreeMap<>();

    public void add(FourCharsPhrase item) {
      boolean found = false;
      for (FourCharsPhrase r : representants) {
        if (r.isEquivalentUnderSyncronizedRotation(item)) {
          found = true;
          instances.get(r.toString()).add(item);
        }
      }
      if (!found) {
        TreeSet<FourCharsPhrase> inst = new TreeSet<>();
        inst.add(item);
        instances.put(item.toString(), inst);
        representants.add(item);
      }
    }

    public ArrayList<TreeSet<FourCharsPhrase>> getTreeSets() {
      ArrayList<TreeSet<FourCharsPhrase>> o = new ArrayList<>();
      o.addAll(instances.values());
      return o;
    }
  }
  public static FourCharsPhrase fromBinaryWord(Alphabet.Name alphabetName, BinaryWord r){
    FourCharsPhrase output = new FourCharsPhrase(alphabetName);
    var abc = Alphabet.getAlphabet(alphabetName);
    
    if(!abc.isInformationNatural())
      throw new IllegalArgumentException();
    
    var sz_t = (((int)Math.round(abc.information()))*4);
    if(r.getN() % sz_t != 0) {
      throw new RuntimeException("Rhythm's size is not divisible by " + Integer.toString(sz_t));
    }
    
    int k = 0;
    
    while(k<r.getN()) {
      var t = new BinaryWord(new BitSet(),sz_t);
      for(int i=0;i<sz_t;i++) {
        if(r.get(k)) {
          t.set(k%sz_t, true);
        }
        k++;
      }
      output.add(new FourChars(t.toWord(alphabetName)));
    }
    return output;
  }
  
  public static ArrayList<FourCharsPhrase> fromRhythmArray(
      Alphabet.Name alphabetName, ArrayList<BinaryWord> list) {
    ArrayList<FourCharsPhrase> o = new ArrayList<>();
    
    for(BinaryWord r : list) {
      o.add(FourCharsPhrase.fromBinaryWord(alphabetName,r));
    }
    return o;
  }
  
  public static ArrayList<FourCharsPhrase> clusterRhythmPartition(
      Alphabet.Name alphabetName, ArrayList<BinaryWord> _partition) {
    if(_partition == null) throw new RuntimeException("clusterRhythmPartition:: partition is null.");
    ArrayList<FourCharsPhrase> partition = FourCharsPhrase.fromRhythmArray(alphabetName, _partition);
    if(partition.size()==1) {
      ArrayList<FourCharsPhrase> f = new ArrayList<>();
      f.add(partition.get(0));
      return f;
    }
    
    FourCharsentenceUnionSet us = new FourCharsentenceUnionSet();
    for(FourCharsPhrase r: partition) {us.add(r);}
    ArrayList<FourCharsPhrase> o = new ArrayList<FourCharsPhrase>();
    
    for(TreeSet<FourCharsPhrase> t : us.getTreeSets()) {
      FourCharsPhrase s = null;
      for(FourCharsPhrase l : t) {
        if(s==null) {s = (FourCharsPhrase)l.clone();}
        s = FourCharsPhrase.or(s, l);
      }
      o.add(s);
    }
    
    return CollectionUtils.reverse(o);
  }

}
