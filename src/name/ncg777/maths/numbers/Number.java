package name.ncg777.maths.numbers;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import name.ncg777.maths.Combination;
import name.ncg777.maths.sequences.Sequence;

public class Number extends ArrayList<Character> implements Serializable, Comparable<Number> {

  private static final long serialVersionUID = 1L;

  protected Alphabet.Name alphabetName;

  public Alphabet getAlphabet() {
    return Alphabet.getAlphabet(alphabetName);
  }

  public Number(Alphabet.Name alphabetName) {
    super();
    this.alphabetName = alphabetName;
  }

  public Number(Number number) {
    this(number.alphabetName, number);
  }

  public Number(Alphabet.Name alphabetName, Character[] array) {
    this(alphabetName);
    for (var c : array)
      this.add(c);
  }

  public Number(Alphabet.Name alphabetName, List<Character> list) {
    this(alphabetName);
    for (var c : list)
      this.add(c);
  }
  
  public Number(Alphabet.Name alphabetName, String string) {
    this(alphabetName);

    for (int i=string.length()-1; i >=0 ; i--)
      this.add(string.charAt(i));
   }

  public Number(Alphabet.Name alphabetName, BigInteger natural, int length) {
    super();
    this.alphabetName = alphabetName;
    var alphabet = Alphabet.getAlphabet(alphabetName);
    int n = alphabet.size();
    if(natural.compareTo(BigInteger.valueOf(n).pow(length)) >= 0)
      throw new IllegalArgumentException("Not enough bits to encode natural.");
    while (length-- > 0) {
      BigInteger r = natural.mod(BigInteger.valueOf(n));
      this.add(alphabet.get(r.intValue()));
      natural = natural.subtract(r).divide(BigInteger.valueOf(n));
    }
  }

  public Number(Alphabet.Name alphabetName, Combination combination) {
    this.alphabetName = alphabetName;
    var alphabet = Alphabet.getAlphabet(alphabetName);
    
    if (!alphabet.isInformationBinary())
      throw new UnsupportedOperationException("Alphabet size must be a power of 2.");
    
    int b = (int) alphabet.information();
    if (combination.getN() % b != 0) throw new UnsupportedOperationException(
        "Combination size must be multiple of information of alphabet.");
    
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

  public BinaryNumber toBinaryWord() {
    return new BinaryNumber(
        toNatural(),
        BigInteger
          .valueOf(Alphabet.getAlphabet(alphabetName).size())
            .pow(this.size())
            .subtract(BigInteger.ONE)
            .bitLength()
    );
  }

  public BigInteger toNatural() {
    var alphabet = Alphabet.getAlphabet(alphabetName);

    int k = 0;
    var sequence = this.toSequence();
    BigInteger sum = BigInteger.ZERO;
    while (k++ < sequence.size()) {
      var p = BigInteger.valueOf(alphabet.size()).pow(k-1);
      sum = sum.add(BigInteger.valueOf(sequence.get(k-1)).multiply(p));
    }
    return sum;
  }

  public Sequence getContour() {
    var bn = toBinaryWord();
    if (bn.getK() == 0) return new Sequence();
    return bn.getComposition().asSequence().reverse().cyclicalDifference().signs();
  }

  public Sequence getShadowContour() {
    var combination = toBinaryWord();
    if (combination.getK() == 0) return new Sequence();
    Sequence a = combination.getComposition().asSequence().reverse();

    Sequence mid = new Sequence();
    for (int i = 1; i <= a.size(); i++) {
      mid.add(a.get(i - 1) + a.get(i % a.size()));
    }

    return mid.cyclicalDifference().signs();
  }

  public String toBitstring() {
    return toBinaryWord().toString();
  }

  public static Number fromBitstring(Alphabet.Name alphabetName, String string) {
    return BinaryNumber.build(string).toWord(alphabetName);
  }

  public Number(Alphabet.Name alphabetName, Sequence sequence) {
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

  public static Number agglutinate(Number first, Number second) {
    if (!first.getAlphabet().equals(second.getAlphabet())) throw new IllegalArgumentException();
    var o = new Number(first.alphabetName, second);
    o.addAll(first);
    return o;
  }

  public static Number rotate(Number r, int t) {
    var o = new Number(r);
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
    if (!(_other instanceof Number)) return false;
    var other = (Number) _other;
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

  public static boolean equivalentUnderRotation(Number a, Number b) {
    if (a.size() != b.size()) return false;
    for (int i = 0; i < a.size(); i++) {
      if (a.equals(rotate(b, i))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int compareTo(Number o) {
    return this.toSequence().compareTo(o.toSequence());
  }
}
