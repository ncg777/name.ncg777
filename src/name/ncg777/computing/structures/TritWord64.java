package name.ncg777.computing.structures;

import java.math.BigInteger;
import java.util.Objects;
import name.ncg777.maths.Trit;

/** Immutable signed 64-trit word, least significant trit at index zero. */
public record TritWord64(BigInteger value) {
  public static final int WIDTH = 64;
  public static final BigInteger MODULUS = BigInteger.valueOf(3).pow(WIDTH);
  public static final BigInteger MAX = MODULUS.subtract(BigInteger.ONE).divide(BigInteger.TWO);
  private static final BigInteger THREE = BigInteger.valueOf(3);
  private static final BigInteger[] WEIGHTS = weights();

  public TritWord64 {
    Objects.requireNonNull(value);
    if (value.abs().compareTo(MAX) > 0) throw new IllegalArgumentException("Value does not fit 64 trits");
  }

  private static BigInteger[] weights() {
    BigInteger[] result = new BigInteger[WIDTH];
    result[0] = BigInteger.ONE;
    for (int i = 1; i < WIDTH; i++) result[i] = result[i - 1].multiply(THREE);
    return result;
  }

  public static TritWord64 zero() { return new TritWord64(BigInteger.ZERO); }

  /** Parses up to 64 high-to-low trits; T denotes -1. */
  public static TritWord64 parse(String digits) {
    Objects.requireNonNull(digits);
    if (digits.isEmpty() || digits.length() > WIDTH) throw new IllegalArgumentException("Expected 1 to 64 trits");
    int[] lowFirst = new int[digits.length()];
    for (int i = 0; i < digits.length(); i++) {
      char digit = digits.charAt(digits.length() - i - 1);
      lowFirst[i] = switch (digit) {
        case 'T' -> -1;
        case '0' -> 0;
        case '1' -> 1;
        default -> throw new IllegalArgumentException("Trits must be T, 0 or 1");
      };
    }
    return fromTrits(lowFirst);
  }

  /** Missing high trits are zero; excess trits are rejected. */
  public static TritWord64 fromTrits(int... lowFirst) {
    Objects.requireNonNull(lowFirst);
    if (lowFirst.length > WIDTH) throw new IllegalArgumentException("More than 64 trits");
    BigInteger result = BigInteger.ZERO;
    for (int i = 0; i < lowFirst.length; i++)
      result = result.add(WEIGHTS[i].multiply(BigInteger.valueOf(Trit.BUF(lowFirst[i]))));
    return new TritWord64(result);
  }

  public int trit(int index) {
    if (index < 0 || index >= WIDTH) throw new IndexOutOfBoundsException(index);
    return value.add(MAX).divide(WEIGHTS[index]).mod(THREE).intValueExact() - 1;
  }

  public TritWord64 withTrit(int index, int trit) {
    int previous = trit(index);
    return new TritWord64(value.add(WEIGHTS[index].multiply(BigInteger.valueOf(Trit.BUF(trit) - previous))));
  }

  /** Two's-complement-style wrap, but in balanced base three. */
  public TritWord64 addWrapped(TritWord64 other) {
    Objects.requireNonNull(other);
    return new TritWord64(value.add(other.value).add(MAX).mod(MODULUS).subtract(MAX));
  }

  public TritWord64 negate() { return new TritWord64(value.negate()); }

  @Override public String toString() {
    StringBuilder result = new StringBuilder(WIDTH);
    for (int i = WIDTH - 1; i >= 0; i--) result.append(trit(i) < 0 ? 'T' : trit(i) == 0 ? '0' : '1');
    return result.toString();
  }
}
