package name.ncg777.maths.phrases;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import name.ncg777.maths.Combination;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.words.Alphabet;
import name.ncg777.maths.words.BinaryWord;
import name.ncg777.maths.words.QuartalWord;
import name.ncg777.maths.words.Word;

public class QuartalWordsPhrase extends ArrayList<QuartalWord> implements Comparable<QuartalWordsPhrase> {
  private static final long serialVersionUID = 1L;
  private Alphabet.Name alphabetName;
  
  public QuartalWordsPhrase(Alphabet.Name alphabetName) {
    this.alphabetName = alphabetName;
  }
  
  public QuartalWordsPhrase(Alphabet.Name alphabetName, String string) {
    this(alphabetName);
    string = string.replaceAll("\\s+", "");

    if(string.length() % 4 != 0) throw new IllegalArgumentException();
    
    for(int i=(string.length()/4)-1;i>=0;i--) {
      this.add(new QuartalWord(alphabetName, 
          (new StringBuilder(string.substring(i*4,(i+1)*4)).toString())));
    }
  }
  
  public QuartalWordsPhrase(Alphabet.Name alphabetName, Word word) {
    this(alphabetName, word.toString());
  }
  
  public QuartalWordsPhrase(Alphabet.Name alphabetName, BinaryWord binaryWord) {
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
  
  public static QuartalWordsPhrase expand(QuartalWordsPhrase a, int x, boolean fill) {
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
    
    QuartalWordsPhrase output = new QuartalWordsPhrase(a.alphabetName, o);
    
    return output;
  }
  
  public static QuartalWordsPhrase rotate(QuartalWordsPhrase r, int t) {
    return new QuartalWordsPhrase(r.alphabetName, BinaryWord.build(r.toBinaryWord().rotate(t)));
  }

  public static QuartalWordsPhrase not(QuartalWordsPhrase a) {
    return new QuartalWordsPhrase(a.alphabetName, a.toBinaryWord().invert());
  }

  public static QuartalWordsPhrase and(QuartalWordsPhrase a, QuartalWordsPhrase b) {
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

    QuartalWordsPhrase output = new QuartalWordsPhrase(a.alphabetName);
    for (int i = 0; i < n; i++) {
      output.add(
          new QuartalWord(
              BinaryWord.build(
                  a.get(i).toBinaryWord().intersect(b.get(i).toBinaryWord()).reverse()
              ).toWord(a.alphabetName)
          )
      );
    }
    return output;
  }

  public static QuartalWordsPhrase or(QuartalWordsPhrase a, QuartalWordsPhrase b) {
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

    QuartalWordsPhrase output = new QuartalWordsPhrase(a.alphabetName);
    
    for (int i = 0; i < n; i++) {
      output.add(new QuartalWord(
          BinaryWord.build(
              Combination.merge(
                  a.get(i).toBinaryWord().reverse(), 
                  b.get(i).toBinaryWord().reverse()))
          .toWord(a.alphabetName)));
    }
    return output;
  }

  public static QuartalWordsPhrase xor(QuartalWordsPhrase a, QuartalWordsPhrase b) {
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

    QuartalWordsPhrase output = new QuartalWordsPhrase(a.alphabetName);
    for (int i = 0; i < n; i++) {
      output.add(
          new QuartalWord(
              BinaryWord.build(
                  (a.get(i).toBinaryWord().symmetricDifference(b.get(i).toBinaryWord())).reverse()).toWord(a.alphabetName)
          )
      );
    }
    return output;
  }

  public static QuartalWordsPhrase minus(QuartalWordsPhrase a, QuartalWordsPhrase b) {
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

    QuartalWordsPhrase output = new QuartalWordsPhrase(a.alphabetName);
    for (int i = 0; i < n; i++) {
      output.add(
          new QuartalWord(
              BinaryWord.build(
                  (a.get(i).toBinaryWord().minus(b.get(i).toBinaryWord())).reverse()
              ).toWord(a.alphabetName)
          )
      );
    }
    return output;
  }

  public static QuartalWordsPhrase convolve(QuartalWordsPhrase a, QuartalWordsPhrase b) {
    if(!a.alphabetName.equals(b.alphabetName))
      throw new IllegalArgumentException();
    
    QuartalWordsPhrase carrier = a;
    QuartalWordsPhrase impulse = b;

    BinaryWord b_carrier = carrier.toBinaryWord().reverse();
    BinaryWord b_impulse = impulse.toBinaryWord().reverse();

    BinaryWord o = new BinaryWord(new BitSet(), b_carrier.getN());

    for (int i = 0; i < b_carrier.getN(); i++) {
      for (int j = 0; j < b_impulse.getN(); j++) {
        o.set(
            (i + j) % o.getN(), 
            o.get((i + j) % o.getN()) | (b_carrier.get(i) & b_impulse.get(j))
        );
      }
    }
    
    return new QuartalWordsPhrase(a.alphabetName, o.reverse());
  }

  public boolean isEquivalentUnderSyncronizedRotation(QuartalWordsPhrase other) {
    if(!this.alphabetName.equals(other.alphabetName))
      throw new IllegalArgumentException();
    var abc = Alphabet.getAlphabet(this.alphabetName);
    
    if (this.size() != other.size()) return false;

    for (int i = 0; i < this.size(); i++) {
      QuartalWordsPhrase rot = QuartalWordsPhrase.rotate(other, i * (4*((int)Math.round(abc.information()))));
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
        QuartalWordsPhrase.clusterRhythmPartition(alphabetName, this.toBinaryWord().decomposeIntoHomogeneousRegions());
    var rs = new ArrayList<BinaryWord>();
    for (QuartalWordsPhrase r : clusters)
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
    return o.reverse();
  }

  private static class FourCharPhraseUnionSet {
    ArrayList<QuartalWordsPhrase> representants = new ArrayList<>();
    TreeMap<String, TreeSet<QuartalWordsPhrase>> instances = new TreeMap<>();

    public void add(QuartalWordsPhrase item) {
      boolean found = false;
      for (QuartalWordsPhrase r : representants) {
        if (r.isEquivalentUnderSyncronizedRotation(item)) {
          found = true;
          instances.get(r.toString()).add(item);
        }
      }
      if (!found) {
        TreeSet<QuartalWordsPhrase> inst = new TreeSet<>();
        inst.add(item);
        instances.put(item.toString(), inst);
        representants.add(item);
      }
    }

    public ArrayList<TreeSet<QuartalWordsPhrase>> getTreeSets() {
      ArrayList<TreeSet<QuartalWordsPhrase>> o = new ArrayList<>();
      o.addAll(instances.values());
      return o;
    }
  }
  public static QuartalWordsPhrase fromCombination(Alphabet.Name alphabetName, Combination r){
    QuartalWordsPhrase output = new QuartalWordsPhrase(alphabetName);
    var abc = Alphabet.getAlphabet(alphabetName);
    
    if(!abc.isInformationBinary())
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
      output.add(new QuartalWord(t.toWord(alphabetName)));
    }
    return output;
  }
  
  public static List<QuartalWordsPhrase> fromRhythmList(
      Alphabet.Name alphabetName, List<? extends Combination> list) {
    List<QuartalWordsPhrase> o = new ArrayList<>();
    
    for(var r : list) {
      o.add(QuartalWordsPhrase.fromCombination(alphabetName,r));
    }
    return o;
  }
  
  public static List<QuartalWordsPhrase> clusterRhythmPartition(
      Alphabet.Name alphabetName, List<? extends Combination> _partition) {
    if(_partition == null) throw new RuntimeException("clusterRhythmPartition:: partition is null.");
    List<QuartalWordsPhrase> partition = QuartalWordsPhrase.fromRhythmList(alphabetName, _partition);
    if(partition.size()==1) {
      ArrayList<QuartalWordsPhrase> f = new ArrayList<>();
      f.add(partition.get(0));
      return f;
    }
    
    FourCharPhraseUnionSet us = new FourCharPhraseUnionSet();
    for(QuartalWordsPhrase r: partition) {us.add(r);}
    ArrayList<QuartalWordsPhrase> o = new ArrayList<QuartalWordsPhrase>();
    
    for(TreeSet<QuartalWordsPhrase> t : us.getTreeSets()) {
      QuartalWordsPhrase s = null;
      for(QuartalWordsPhrase l : t) {
        if(s==null) {s = (QuartalWordsPhrase)l.clone();}
        s = QuartalWordsPhrase.or(s, l);
      }
      o.add(s);
    }
    
    return o.reversed();
  }

  @Override
  public int compareTo(QuartalWordsPhrase o) {
    return this.toString().compareTo(o.toString());
  }

}
