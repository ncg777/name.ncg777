package name.ncg777.maths.sentences;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.TreeSet;

import name.ncg777.maths.words.Alphabet;
import name.ncg777.maths.words.BinaryWord;
import name.ncg777.maths.words.Tetragraph;
import name.ncg777.maths.words.Word;

public class TetragraphSentence extends ArrayList<Tetragraph> {
  private static final long serialVersionUID = 1L;
  private Alphabet alphabet;
  
  public TetragraphSentence(Alphabet alphabet) {
    this.alphabet = alphabet;
  }
  
  public TetragraphSentence(Alphabet alphabet, String string) {
    this(alphabet);
    string = string.replaceAll("\\s+", "");
    if(string.length()%4 != 0) throw new IllegalArgumentException();
    for(int i=0;i<string.length() / 4;i++) this.add(new Tetragraph(alphabet, string.substring(i*4,(i*4)+4)));
  }
  
  public TetragraphSentence(Alphabet alphabet, Word word) {
    this(alphabet, word.toString());
  }
  
  public TetragraphSentence(Alphabet alphabet, BinaryWord binaryWord) {
    this(alphabet, binaryWord.toWord(alphabet));
  }
  
  public Word toWord() {
    return new Word(alphabet,toString().replaceAll("\\s", ""));
  }
  
  @Override
  public String toString() {
    var sb = new StringBuilder();
    for(var t : this) sb.append(t.toString(true));
    return sb.toString();
  }
  
  public static TetragraphSentence expand(TetragraphSentence a, int x, boolean fill) {
    int n = x;
    BinaryWord b = a.toWord().toBinaryWord();
    BinaryWord o = new BinaryWord(new BitSet(), n * b.getN());

    for (int i = 0; i < b.getN(); i++) {
      o.set(i * n, b.get(i));
      if(fill) {
        for(int j=1; j<n;j++) {
          o.set((i * n) + j, b.get(i));
        }
      }
    }
    TetragraphSentence output = new TetragraphSentence(a.alphabet);

    for (int i = 0; i < o.getN() / a.alphabet.size(); i++) {
      TreeSet<Integer> t = new TreeSet<Integer>();

      for (int j = 0; j < a.alphabet.size(); j++) {
        if (o.get((i * a.alphabet.size()) + j)) {
          t.add(j);
        }
      }
      output.add(
          new Tetragraph(
              a.alphabet, 
              (new BinaryWord(a.alphabet.size(), t)).toWord(a.alphabet).toString()));
    }
    return output;
  }
  
//public boolean isEquivalentUnderSyncronizedRotation(HexadecimalSentence other) {
//  if(other == null) return false;
//  if(this.size() != other.size()) return false;
//  
//  for(int i=0;i<this.size();i++) {
//    
//    HexadecimalSentence rot = HexadecimalSentence.rotate(other, i*16);
//    boolean eq = true;
//    for(int j=0;j<rot.size();j++) {
//      if(!this.get(j).toString().equals(rot.get(j).toString())) {
//        eq=false;
//        continue;
//      }
//    }
//    if(eq) return true;
//  }
//  return false;
//}
  
//public Sequence clusterPartition() {
//ArrayList<HexadecimalSentence> clusters = HexadecimalSentence.clusterRhythmPartition(this.asRhythm().partitionByEquality());
//ArrayList<BinaryWord> rs= new ArrayList<>();
//for(HexadecimalSentence r : clusters) rs.add(r.asRhythm());
//
//Sequence o = new Sequence();
//
//int n = rs.get(0).getN();
//
//for(int i=0;i<n;i++) {
//  for(int j=0; j<clusters.size();j++) {
//    if(rs.get(j).get(i)) {
//      o.add(j);
//    }
//  }
//}
//return o;
//}

//private static class RhythmHexaListUnionSet {
//
//ArrayList<HexadecimalSentence> representants = new ArrayList<>();
//TreeMap<String,TreeSet<HexadecimalSentence>> instances = new TreeMap<>();
//
//public void add(HexadecimalSentence item) {
//  boolean found = false;
//  for(HexadecimalSentence r : representants) {
//    if(r.isEquivalentUnderSyncronizedRotation(item)) {
//      found=true;
//      instances.get(r.toString()).add(item);
//    }
//  }
//  if(!found) {
//    TreeSet<HexadecimalSentence> inst = new TreeSet<>();
//    inst.add(item);
//    instances.put(item.toString(),inst);
//    representants.add(item);
//  }
//}
//public ArrayList<TreeSet<HexadecimalSentence>> getTreeSets() {
//  ArrayList<TreeSet<HexadecimalSentence>> o = new ArrayList<>();
//  o.addAll(instances.values());
//  return o;
//}
//}

//public static ArrayList<HexadecimalSentence> clusterRhythmPartition(ArrayList<BinaryWord> _partition) {
//if(_partition == null) throw new RuntimeException("clusterRhythmPartition:: partition is null.");
//ArrayList<HexadecimalSentence> partition = HexadecimalSentence.fromRhythmArray(_partition);
//if(partition.size()==1) {
//  ArrayList<HexadecimalSentence> f = new ArrayList<>();
//  f.add(partition.get(0));
//  return f;
//}
//
//
//RhythmHexaListUnionSet us = new RhythmHexaListUnionSet();
//for(HexadecimalSentence r: partition) {us.add(r);}
//ArrayList<HexadecimalSentence> o = new ArrayList<HexadecimalSentence>();
//
//for(TreeSet<HexadecimalSentence> t : us.getTreeSets()) {
//  HexadecimalSentence s = null;
//  for(HexadecimalSentence l : t) {
//    if(s==null) {s = (HexadecimalSentence)l.clone();}
//    s = HexadecimalSentence.or(s, l);
//  }
//  o.add(s);
//}
//
//return CollectionUtils.reverse(o);
//}

}
