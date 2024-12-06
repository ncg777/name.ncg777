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

  protected Alphabet.Name alphabetName;

  public Alphabet getAlphabet() {
    return Alphabet.getAlphabet(alphabetName);
  }

  public Word(Alphabet.Name alphabetName) {
    super();
    this.alphabetName = alphabetName;
  }

  public Word(Word word) {
    this(word.alphabetName, word);
  }

  public Word(Alphabet.Name alphabetName, Character[] array) {
    this(alphabetName);
    for (var c : array)
      this.add(c);
  }

  public Word(Alphabet.Name alphabetName, List<Character> list) {
    this(alphabetName);
    for (var c : list)
      this.add(c);
  }
  
  public Word(Alphabet.Name alphabetName, String string) {
    this(alphabetName);

    for (int i=string.length()-1; i >=0 ; i--)
      this.add(string.charAt(i));
   }

  public Word(Alphabet.Name alphabetName, long natural, int length) {
    super();
    this.alphabetName = alphabetName;
    var alphabet = Alphabet.getAlphabet(alphabetName);
    int n = alphabet.size();
    while (length-- > 0) {
      long r = natural % n;
      this.add(alphabet.get((int) r));
      natural = (natural - r) / n;
    }
  }

  public Word(Alphabet.Name alphabetName, Combination combination) {
    this.alphabetName = alphabetName;
    var alphabet = Alphabet.getAlphabet(alphabetName);
    
    if (!alphabet.isInformationNatural())
      throw new UnsupportedOperationException("Alphabet size must be a power of 2.");
    
    int b = (int) alphabet.information();
    if (combination.getN() % b != 0) throw new UnsupportedOperationException(
        "Combination size must be multiple of bitness of alphabet.");
    
    int n = combination.getN();
    int length = n / b;
    var sb = new StringBuilder(combination.toBinaryString()).reverse();
    ArrayList<Character> tmp = new ArrayList<Character>();
    for (int i = 0; i < length; i++) {
      tmp.add(
          alphabet.get(
              Integer.parseInt(sb.substring(i * b, (i + 1) * b), 2)
          ));
    }
    
    this.addAll(tmp.reversed());
  }

  public BinaryWord toBinaryWord() {
    var abc = Alphabet.getAlphabet(alphabetName);
    
    if (!abc.isInformationNatural())
      throw new UnsupportedOperationException();

    int b = (int)Math.round(abc.information());
    var o = new BinaryWord(new BitSet(), this.size()*b);
    
    for(int i=0;i<this.size();i++) {
      StringBuilder sb = new StringBuilder(Integer.toBinaryString(abc.indexOf(this.get(i))));
      while(sb.length() < b) sb.insert(0, "0");
      for(int j=0;j<b;j++) o.set((i*b)+j, sb.charAt(-1+b-j) == '1');
    }
    return o;
  }

  public long toNatural() {
    var alphabet = Alphabet.getAlphabet(alphabetName);
    if (!alphabet.isInformationNatural()) throw new UnsupportedOperationException(
        "Can only convert word to natural number if alphabet size is a power of 2.");

    int k = 0;
    var sequence = this.toSequence();
    long sum = 0;
    while (++k < sequence.size()) {
      sum = checkedAdd(sum, (long) (sequence.get(k-1) * checkedPow(alphabet.size(), k-1)));
    }
    return sum;
  }

  public Sequence getContour() {
    var bn = toBinaryWord();
    if (bn.getK() == 0) return new Sequence();
    return bn.getComposition().asSequence().cyclicalDifference().signs();
  }

  public Sequence getShadowContour() {
    var combination = toBinaryWord();
    if (combination.getK() == 0) return new Sequence();
    Sequence a = combination.getComposition().asSequence();

    Sequence mid = new Sequence();
    for (int i = 1; i <= a.size(); i++) {
      mid.add(a.get(i - 1) + a.get(i % a.size()));
    }

    return mid.cyclicalDifference().signs();
  }

  public String toBitstring() {
    return toBinaryWord().toString();
  }

  public static Word fromBitstring(Alphabet.Name alphabetName, String string) {
    return BinaryWord.build(string).toWord(alphabetName);
  }

  public Word(Alphabet.Name alphabetName, Sequence sequence) {
    super();
    this.alphabetName = alphabetName;
    var alphabet = Alphabet.getAlphabet(alphabetName);
    if (!sequence.isNatural() || sequence.getMax() >= alphabet.size())
      throw new IllegalArgumentException();
    for (var i : sequence)
      this.add(alphabet.get(i));
  }

  public Sequence toSequence() {
    var alphabet = Alphabet.getAlphabet(alphabetName);
    var o = new Sequence();
    for (int i = 0; i < this.size(); i++) {
      o.add(alphabet.indexOf(this.get(i)));
    }
    return o;
  }

  public static Word agglutinate(Word first, Word second) {
    if (!first.getAlphabet().equals(second.getAlphabet())) throw new IllegalArgumentException();
    var o = new Word(first.alphabetName, second);
    o.addAll(first);
    return o;
  }

  public static Word rotate(Word r, int t) {
    var o = new Word(r);
    if (t < 0) {
      do {
        o.add(o.remove(0));
      } while (++t < 0);
    }
    if (t > 0) {
      do {
        o.add(0, o.removeLast());
      } while (--t > 0);
    }
    return o;
  }

  @Override
  public boolean equals(Object _other) {
    if (!(_other instanceof Word)) return false;
    var other = (Word) _other;
    if (this.size() != other.size()) return false;
    for (int i = 0; i < this.size(); i++)
      if (this.get(i) != other.get(i)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (var c : this)
      sb.append(c);
    
    return sb.reverse().toString();
  }

  public static boolean equivalentUnderRotation(Word a, Word b) {
    if (a.size() != b.size()) return false;
    for (int i = 0; i < a.size(); i++) {
      if (a.equals(rotate(b, i))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int compareTo(Word o) {
    return this.toSequence().compareTo(o.toSequence());
  }
}
