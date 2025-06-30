package name.ncg777.maths.numbers;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import name.ncg777.maths.Combination;
import name.ncg777.maths.sequences.Sequence;

public class Natural extends ArrayList<Character> implements Serializable, Comparable<Natural> {

  private static final long serialVersionUID = 1L;

  protected Cipher.Name cipherName;

  public Cipher getCipher() {
    return Cipher.getCipher(cipherName);
  }

  public Natural(Cipher.Name cipherName) {
    super();
    this.cipherName = cipherName;
  }

  public Natural(Natural natural) {
    this(natural.cipherName, natural);
  }

  public Natural(Cipher.Name cipherName, Character[] array) {
    this(cipherName);
    for (var c : array)
      this.add(c);
  }

  public Natural(Cipher.Name cipherName, List<Character> list) {
    this(cipherName);
    for (var c : list)
      this.add(c);
  }
  
  public Natural(Cipher.Name cipherName, String string) {
    this(cipherName);

    for (int i=string.length()-1; i >=0 ; i--)
      this.add(string.charAt(i));
   }

  public Natural(Cipher.Name cipherName, BigInteger natural, int length) {
    super();
    this.cipherName = cipherName;
    var cipher = Cipher.getCipher(cipherName);
    int n = cipher.size();
    if(natural.compareTo(BigInteger.valueOf(n).pow(length)) >= 0)
      throw new IllegalArgumentException("Not enough bits to encode natural.");
    while (length-- > 0) {
      BigInteger r = natural.mod(BigInteger.valueOf(n));
      this.add(cipher.get(r.intValue()));
      natural = natural.subtract(r).divide(BigInteger.valueOf(n));
    }
  }

  public Natural(Cipher.Name cipherName, Combination combination) {
    this.cipherName = cipherName;
    var cipher = Cipher.getCipher(cipherName);
    
    if (!cipher.isInformationBinary())
      throw new UnsupportedOperationException("Cipher size must be a power of 2.");
    
    int b = (int) cipher.information();
    if (combination.getN() % b != 0) throw new UnsupportedOperationException(
        "Combination size must be multiple of information of cipher.");
    
    int n = combination.getN();
    int length = n / b;
    var sb = new StringBuilder(combination.toBinaryString()).reverse();
    ArrayList<Character> tmp = new ArrayList<Character>();
    for (int i = 0; i < length; i++) {
      tmp.add(
          cipher.get(
              Integer.parseInt(sb.substring(i * b, (i + 1) * b), 2)
          ));
    }
    
    Collections.reverse(tmp);
    this.addAll(tmp);
  }

  public BinaryNatural toBinaryNatural() {
    return new BinaryNatural(
        toBigInteger(),
        BigInteger
          .valueOf(Cipher.getCipher(cipherName).size())
            .pow(this.size())
            .subtract(BigInteger.ONE)
            .bitLength()
    );
  }

  public BigInteger toBigInteger() {
    var cipher = Cipher.getCipher(cipherName);

    int k = 0;
    var sequence = this.toSequence();
    BigInteger sum = BigInteger.ZERO;
    while (k++ < sequence.size()) {
      var p = BigInteger.valueOf(cipher.size()).pow(k-1);
      sum = sum.add(BigInteger.valueOf(sequence.get(k-1)).multiply(p));
    }
    return sum;
  }

  public Sequence getContour() {
    var bn = toBinaryNatural();
    if (bn.getK() == 0) return new Sequence();
    return bn.getComposition().asSequence().reverse().cyclicalDifference().signs();
  }

  public Sequence getShadowContour() {
    var combination = toBinaryNatural();
    if (combination.getK() == 0) return new Sequence();
    Sequence a = combination.getComposition().asSequence().reverse();

    Sequence mid = new Sequence();
    for (int i = 1; i <= a.size(); i++) {
      mid.add(a.get(i - 1) + a.get(i % a.size()));
    }

    return mid.cyclicalDifference().signs();
  }

  public String toBitstring() {
    return toBinaryNatural().toString();
  }

  public static Natural fromBitstring(Cipher.Name cipherName, String string) {
    return BinaryNatural.build(string).toNatural(cipherName);
  }

  public Natural(Cipher.Name cipherName, Sequence sequence) {
    super();
    this.cipherName = cipherName;
    var cipher = Cipher.getCipher(cipherName);
    if (!sequence.isNatural() || sequence.getMax() >= cipher.size())
      throw new IllegalArgumentException();
    for (var i : sequence)
      this.add(cipher.get(i));
  }

  public Sequence toSequence() {
    var cipher = Cipher.getCipher(cipherName);
    var o = new Sequence();
    for (int i = 0; i < this.size(); i++) {
      o.add(cipher.indexOf(this.get(i)));
    }
    return o;
  }

  public static Natural agglutinate(Natural first, Natural second) {
    if (!first.getCipher().equals(second.getCipher())) throw new IllegalArgumentException();
    var o = new Natural(first.cipherName, second);
    o.addAll(first);
    return o;
  }

  public static Natural rotate(Natural r, int t) {
    var o = new Natural(r);
    if (t < 0) {
      do {
        o.add(o.remove(0));
      } while (++t < 0);
    }
    if (t > 0) {
      do {
        o.add(0, o.remove(o.size()-1));
      } while (--t > 0);
    }
    return o;
  }

  @Override
  public boolean equals(Object _other) {
    if (!(_other instanceof Natural)) return false;
    var other = (Natural) _other;
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

  public static boolean equivalentUnderRotation(Natural a, Natural b) {
    if (a.size() != b.size()) return false;
    for (int i = 0; i < a.size(); i++) {
      if (a.equals(rotate(b, i))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int compareTo(Natural o) {
    return this.toSequence().compareTo(o.toSequence());
  }
}
