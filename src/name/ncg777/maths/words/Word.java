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
    if(Alphabet.isStringReversed(alphabetName)) string = (new StringBuilder(string)).reverse().toString();
    for (int i = 0; i < string.length(); i++)
      this.add(string.charAt(i));
  }

  public Word(Alphabet.Name alphabetName, long natural, int length) {
    super();
    var alphabet = Alphabet.getAlphabet(alphabetName);
    int n = alphabet.size();
    while (length-- > 0) {
      long r = natural % n;
      this.add(alphabet.get((int) r));
      natural = (natural - r) / n;
    }
  }

  public Word(Alphabet.Name alphabetName, Combination combination) {
    var alphabet = Alphabet.getAlphabet(alphabetName);
    if (!alphabet.isBitnessANatural())
      throw new UnsupportedOperationException("Alphabet size must be a power of 2.");
    int b = (int) alphabet.bitness();
    if (combination.getN() % b != 0) throw new UnsupportedOperationException(
        "Combination size must be multiple of bitness of alphabet.");
    int n = combination.getN();
    int length = n / b;
    String bitstring = new StringBuilder(combination.toBinaryString()).reverse().toString();

    for (int i = 0; i < length; i++) {
      this.add(alphabet.get(Integer.parseInt(bitstring.substring((i * b), (i + 1) * b), 2)));
    }
  }

  public BinaryWord toBinaryWord() {
    var combination = toCombination();
    return new BinaryWord(combination, combination.getN());
  }

  public long toNatural() {
    var alphabet = Alphabet.getAlphabet(alphabetName);
    if (!alphabet.isBitnessANatural()) throw new UnsupportedOperationException(
        "Can only convert word to natural number if alphabet size is a power of 2.");

    int k = 0;
    var sequence = this.toSequence();
    long sum = 0;
    while (k++ < sequence.size()) {
      sum = checkedAdd(sum, (long) (sequence.get(k) * checkedPow(alphabet.size(), k)));
    }
    return sum;
  }

  public Sequence getContour() {
    var combination = toCombination();
    if (combination.getK() == 0) return new Sequence();
    return combination.getComposition().asSequence().cyclicalDifference().signs();
  }

  public Sequence getShadowContour() {
    var combination = toCombination();
    if (combination.getK() == 0) return new Sequence();
    Sequence a = combination.getComposition().asSequence();

    Sequence mid = new Sequence();
    for (int i = 1; i <= a.size(); i++) {
      mid.add(a.get(i - 1) + a.get(i % a.size()));
    }

    return mid.cyclicalDifference().signs();
  }

  public Combination toCombination() {
    var alphabet = Alphabet.getAlphabet(alphabetName);
    if (!alphabet.isBitnessANatural()) throw new UnsupportedOperationException(
        "Can only convert word to combination if alphabet size is a power of 2.");

    int n = ((int) Math.round(alphabet.bitness())) * this.size();
    String string = toBitString(alphabet, n);
    BitSet bs = new BitSet(n);
    for (int i = 0; i < n; i++) {
      bs.set(i, string.charAt(-1 + n - i) == '1');
    }
    return new Combination(bs, n);
  }

  public String toBitString(Alphabet alphabet, int length) {
    return (new Word(Alphabet.Name.Binary, toNatural(), length)).toString();
  }

  public static Word fromBitString(Alphabet.Name alphabetName, String string, int length) {
    var sequence = new Sequence();
    for (int i = string.length() - 1; i >= 0; i--) {
      char c = string.charAt(i);
      if (!(c == '0' || c == '1')) throw new IllegalArgumentException();
      sequence.add(c == '1' ? 1 : 0);
    }

    return new Word(alphabetName, (new Word(Alphabet.Name.Binary, sequence)).toNatural(), length);
  }

  public Word(Alphabet.Name alphabetName, Sequence sequence) {
    super();
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
    var o = new Word(first.alphabetName, first);
    o.addAll(second);
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
    var w = new Word(this);
    if (Alphabet.isStringReversed(alphabetName)) {
      var x = w.toBinaryWord();
      var combination = new Combination(x.getN());
      for (int i = x.nextSetBit(0); i >= 0; i = x.nextSetBit(i + 1))
        combination.set(-1 + x.getN() - i, x.get(i));
      w = (new BinaryWord(combination, x.getN())).toWord(alphabetName);
    }

    StringBuilder sb = new StringBuilder();
    for (var c : w)
      sb.append(c);
    return sb.toString();
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