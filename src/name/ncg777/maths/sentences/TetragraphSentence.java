package name.ncg777.maths.sentences;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.TreeMap;
import java.util.TreeSet;

import name.ncg777.computing.structures.CollectionUtils;
import name.ncg777.maths.Combination;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.words.Alphabet;
import name.ncg777.maths.words.BinaryWord;
import name.ncg777.maths.words.Tetragraph;
import name.ncg777.maths.words.Word;

public class TetragraphSentence extends ArrayList<Tetragraph> {
  private static final long serialVersionUID = 1L;
  private Alphabet.Name alphabetName;
  
  public TetragraphSentence(Alphabet.Name alphabetName) {
    this.alphabetName = alphabetName;
  }
  
  public TetragraphSentence(Alphabet.Name alphabetName, String string) {
    this(alphabetName);
    string = string.replaceAll("\\s+", "");
    if(string.length()%4 != 0) throw new IllegalArgumentException();
    for(int i=0;i<string.length() / 4;i++) this.add(new Tetragraph(alphabetName, string.substring(i*4,(i+1)*4)));
  }
  
  public TetragraphSentence(Alphabet.Name alphabetName, Word word) {
    this(alphabetName, word.toString());
  }
  
  public TetragraphSentence(Alphabet.Name alphabetName, BinaryWord binaryWord) {
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
    for(var t : this) sb.append(t.toString(true) + " ");
    return sb.toString().trim();
  }
  
  public static TetragraphSentence expand(TetragraphSentence a, int x, boolean fill) {
    var abc = Alphabet.getAlphabet(a.alphabetName);
    var bitness = (int)Math.round(abc.information());
    
    BinaryWord b = a.toWord().toBinaryWord();
    BinaryWord o = new BinaryWord(new BitSet(), x * b.getN());
    
    for (int i = 0; i < b.getN(); i++) {
      o.set(i * x, b.get(i));
      if(fill) {
        for(int j=1; j<x;j++) {
          o.set((i * x) + j, b.get(i));
        }
      }
    }
    TetragraphSentence output = new TetragraphSentence(a.alphabetName);
    
    for (int i = 0; i < o.getN() / (bitness*4); i++) {
      var bn = new BinaryWord(new BitSet(), bitness*4);

      for (int j = 0; j < (bitness*4); j++) {
        if (o.get((i *  (bitness*4)) + j)) {
          bn.set(j);
        }
      }
      output.add(new Tetragraph(bn.toWord(a.alphabetName)));
    }
    return output;
  }
  
  public static TetragraphSentence rotate(TetragraphSentence r, int t) {
    return new TetragraphSentence(r.alphabetName, BinaryWord.build(r.toBinaryWord().rotate(t)));
  }

  public static TetragraphSentence not(TetragraphSentence a) {
    return new TetragraphSentence(a.alphabetName, a.toBinaryWord().invert());
  }

  public static TetragraphSentence and(TetragraphSentence a, TetragraphSentence b) {
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

    TetragraphSentence output = new TetragraphSentence(a.alphabetName);
    for (int i = 0; i < n; i++) {
      output.add(
          new Tetragraph(
              BinaryWord.build(
                  a.get(i).toBinaryWord().intersect(b.get(i).toBinaryWord())
              ).toWord(a.alphabetName)
          )
      );
    }
    return output;
  }

  public static TetragraphSentence or(TetragraphSentence a, TetragraphSentence b) {
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

    TetragraphSentence output = new TetragraphSentence(a.alphabetName);
    for (int i = 0; i < n; i++) {
      output.add(
          new Tetragraph(
              BinaryWord.build(
                  Combination.merge(a.get(i).toBinaryWord(), b.get(i).toBinaryWord())
              ).toWord(a.alphabetName)
          )
      );
    }
    return output;
  }

  public static TetragraphSentence xor(TetragraphSentence a, TetragraphSentence b) {
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

    TetragraphSentence output = new TetragraphSentence(a.alphabetName);
    for (int i = 0; i < n; i++) {
      output.add(
          new Tetragraph(
              BinaryWord.build(
                  (a.get(i).toBinaryWord().symmetricDifference(b.get(i).toBinaryWord()))
              ).toWord(a.alphabetName)
          )
      );
    }
    return output;
  }

  public static TetragraphSentence minus(TetragraphSentence a, TetragraphSentence b) {
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

    TetragraphSentence output = new TetragraphSentence(a.alphabetName);
    for (int i = 0; i < n; i++) {
      output.add(
          new Tetragraph(
              BinaryWord.build(
                  (a.get(i).toBinaryWord().minus(b.get(i).toBinaryWord()))
              ).toWord(a.alphabetName)
          )
      );
    }
    return output;
  }

  public static TetragraphSentence convolve(TetragraphSentence a, TetragraphSentence b) {
    if(!a.alphabetName.equals(b.alphabetName))
      throw new IllegalArgumentException();
    
    TetragraphSentence carrier = a;
    TetragraphSentence impulse = b;

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
    
    return new TetragraphSentence(a.alphabetName, output);
  }

  public boolean isEquivalentUnderSyncronizedRotation(TetragraphSentence other) {
    if(this.alphabetName.equals(other.alphabetName))
      throw new IllegalArgumentException();
    var abc = Alphabet.getAlphabet(this.alphabetName);
    
    if (this.size() != other.size()) return false;

    for (int i = 0; i < this.size(); i++) {
      TetragraphSentence rot = TetragraphSentence.rotate(other, i * (4*((int)Math.round(abc.information()))));
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
        TetragraphSentence.clusterRhythmPartition(alphabetName, this.toBinaryWord().partitionByEquality());
    ArrayList<BinaryWord> rs = new ArrayList<>();
    for (TetragraphSentence r : clusters)
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

  private static class TetragraphSentenceUnionSet {
    ArrayList<TetragraphSentence> representants = new ArrayList<>();
    TreeMap<String, TreeSet<TetragraphSentence>> instances = new TreeMap<>();

    public void add(TetragraphSentence item) {
      boolean found = false;
      for (TetragraphSentence r : representants) {
        if (r.isEquivalentUnderSyncronizedRotation(item)) {
          found = true;
          instances.get(r.toString()).add(item);
        }
      }
      if (!found) {
        TreeSet<TetragraphSentence> inst = new TreeSet<>();
        inst.add(item);
        instances.put(item.toString(), inst);
        representants.add(item);
      }
    }

    public ArrayList<TreeSet<TetragraphSentence>> getTreeSets() {
      ArrayList<TreeSet<TetragraphSentence>> o = new ArrayList<>();
      o.addAll(instances.values());
      return o;
    }
  }
  public static TetragraphSentence fromBinaryWord(Alphabet.Name alphabetName, BinaryWord r){
    TetragraphSentence output = new TetragraphSentence(alphabetName);
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
      output.add(new Tetragraph(t.toWord(alphabetName)));
    }
    return output;
  }
  
  public static ArrayList<TetragraphSentence> fromRhythmArray(
      Alphabet.Name alphabetName, ArrayList<BinaryWord> list) {
    ArrayList<TetragraphSentence> o = new ArrayList<>();
    
    for(BinaryWord r : list) {
      o.add(TetragraphSentence.fromBinaryWord(alphabetName,r));
    }
    return o;
  }
  
  public static ArrayList<TetragraphSentence> clusterRhythmPartition(
      Alphabet.Name alphabetName, ArrayList<BinaryWord> _partition) {
    if(_partition == null) throw new RuntimeException("clusterRhythmPartition:: partition is null.");
    ArrayList<TetragraphSentence> partition = TetragraphSentence.fromRhythmArray(alphabetName, _partition);
    if(partition.size()==1) {
      ArrayList<TetragraphSentence> f = new ArrayList<>();
      f.add(partition.get(0));
      return f;
    }
    
    TetragraphSentenceUnionSet us = new TetragraphSentenceUnionSet();
    for(TetragraphSentence r: partition) {us.add(r);}
    ArrayList<TetragraphSentence> o = new ArrayList<TetragraphSentence>();
    
    for(TreeSet<TetragraphSentence> t : us.getTreeSets()) {
      TetragraphSentence s = null;
      for(TetragraphSentence l : t) {
        if(s==null) {s = (TetragraphSentence)l.clone();}
        s = TetragraphSentence.or(s, l);
      }
      o.add(s);
    }
    
    return CollectionUtils.reverse(o);
  }

}
