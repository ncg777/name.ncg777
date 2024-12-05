package name.ncg777.maths.words;

import static com.google.common.math.LongMath.checkedPow;
import static com.google.common.math.LongMath.checkedAdd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import name.ncg777.maths.Combination;
import name.ncg777.maths.sequences.Sequence;

public class Word extends ArrayList<Character> implements Serializable, Comparable<Word> {
  
  private static final long serialVersionUID = 1L;
  
  private Alphabet alphabet;
  public Alphabet getAlphabet() {return alphabet; }
  public Word(Alphabet alphabet) {
    super();
    this.alphabet = alphabet;
  }
  
  public Word(Word word) {
    this(word.getAlphabet(),word);
  }
  public Word(Alphabet alphabet, Character[] array) {
    this(alphabet);
    for(var c : array) this.add(c);
  }
  
  public Word(Alphabet alphabet, List<Character> list) {
    this(alphabet);
    for(var c : list) this.add(c);
  }
  public Word(Alphabet alphabet, String string) {
    this(alphabet);
    
    for(int i=0; i<string.length();i++) this.add(string.charAt(i));
  }
  
  public Word(Alphabet alphabet, long natural, int length) {
    super();
    int n = alphabet.size();
    while(length-- > 0) {
      long r = natural % n;
      this.add(alphabet.get((int)r));
      natural = (natural - r) / n;
    }
  }

  public Word(Alphabet alphabet, Combination combination) {
    if(!alphabet.isBitnessANatural()) throw new UnsupportedOperationException("Alphabet size must be a power of 2.");
    int b = (int)alphabet.bitness();
    if(combination.getN() % b != 0)  throw new UnsupportedOperationException("Combination size must be multiple of bitness of alphabet.");
    int n = combination.getN();
    int length = n / b;
    String bitstring = new StringBuilder(combination.toBinaryString()).reverse().toString();
    
    for(int i=0;i<length;i++) {
      this.add(alphabet.get(Integer.parseInt(bitstring.substring((i*b), (i+1)*b), 2)));
    }
  }
  
  public BinaryWord toBinaryWord() {
    var combination = toCombination();
    return new BinaryWord(combination, combination.getN());
  }
  
  public long toNatural() {
    if(!alphabet.isBitnessANatural()) 
      throw new UnsupportedOperationException("Can only convert word to natural number if alphabet size is a power of 2.");
    
    int k = 0;
    var sequence = this.toSequence();
    long sum = 0;
    while(k++ < sequence.size()) {
      sum = checkedAdd(sum, (long)(sequence.get(k)*checkedPow(alphabet.size(), k)));
    }
    return sum;
  }
  
  public Sequence getContour() {
    var combination = toCombination();
    if(combination.getK() == 0) return new Sequence();
    return combination.getComposition().asSequence().cyclicalDifference().signs();
  }
  
  public Sequence getShadowContour() {
    var combination = toCombination();
    if(combination.getK() == 0) return new Sequence();
    Sequence a = combination.getComposition().asSequence();

    Sequence mid = new Sequence();
    for(int i=1;i<=a.size();i++) {
      mid.add(a.get(i-1) + a.get(i%a.size()));
    }
    
    return mid.cyclicalDifference().signs();
  }
  
  public Combination toCombination() {
    if(!alphabet.isBitnessANatural()) 
      throw new UnsupportedOperationException("Can only convert word to combination if alphabet size is a power of 2.");
    
    int n = ((int) Math.round(alphabet.bitness())) *this.size();
    String string = toBitString(alphabet, n);
    BitSet bs = new BitSet(n);
    for(int i=0;i<n;i++) {
      bs.set(i, string.charAt(-1+n-i) == '1');
    }
    return new Combination(bs, n);
  }
  
  public String toBitString(Alphabet alphabet, int length) {
    return (new Word(Alphabet.Binary,
        toNatural(),
        length)).toString();
  }
  
  public static Word fromBitString(Alphabet alphabet, String string, int length) {
    var sequence = new Sequence();
    for(int i=string.length()-1;i>=0;i--) {
      char c = string.charAt(i);
      if(!(c == '0' || c == '1')) throw new IllegalArgumentException();
      sequence.add(c == '1' ? 1 : 0);
    }
    
    return new Word(
        alphabet,
        (new Word(Alphabet.Binary,sequence)).toNatural(),
        length);
  }
  
  public Word(Alphabet alphabet, Sequence sequence) {
    super();
    if(!sequence.isNatural() || sequence.getMax() >= alphabet.size()) throw new IllegalArgumentException();
    for(var i : sequence) this.add(alphabet.get(i));
  }
  
  public Sequence toSequence() {
    var o = new Sequence();
    for(int i=0;i<this.size();i++) {o.add(alphabet.indexOf(this.get(i)));}
    return o;
  }
    
  public static Word agglutinate(Word first, Word second) {
    if(!first.getAlphabet().equals(second.getAlphabet())) throw new IllegalArgumentException();
    var o = new Word(first.getAlphabet(),first);
    o.addAll(second);
    return o;
  }

  public static Word rotate(Word r, int t) {
    var o = new Word(r);
    if(t<0) {
      do {
        o.add(o.remove(0));
      } while(++t < 0);
    }
    if(t>0) {
      do {
        o.add(0,o.removeLast());
      } while(--t > 0);
    }
    return o;
  }

  @Override
  public boolean equals(Object _other) {
    if(!(_other instanceof Word)) return false;
    var other = (Word) _other;
    if(this.size() != other.size()) return false;
    for(int i=0;i<this.size();i++) if(this.get(i) != other.get(i)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for(var c : this) sb.append(c);
    return sb.reverse().toString();
  }

  public static boolean equivalentUnderRotation(Word a, Word b) {
    if(a.size()!=b.size()) return false;
    for (int i = 0; i < a.size(); i++) {
      if (a.equals(rotate(b, i))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int compareTo(Word o) {
    
    return 0;
  }
}
