package name.ncg777.maths.words;

import java.util.BitSet;
import java.util.Set;

import name.ncg777.maths.Numbers;

public class PureDupleBinaryWord extends BinaryWord {
  private static final long serialVersionUID = 7627744764102568624L;

  public PureDupleBinaryWord(BinaryWord binaryWord) {
    super(binaryWord);
    if(!Numbers.isPowerOfTwo(binaryWord.getN()))
      throw new IllegalArgumentException();
  }

  public PureDupleBinaryWord(BitSet b, int n) {
    super(b, n);
    if(!Numbers.isPowerOfTwo(n))
      throw new IllegalArgumentException();}

  public PureDupleBinaryWord(Boolean[] b) {
    super(b);
    if(!Numbers.isPowerOfTwo(b.length))
      throw new IllegalArgumentException();
  }

  public PureDupleBinaryWord(long natural, int length) {
    super(natural, length);
    if(!Numbers.isPowerOfTwo(length))
      throw new IllegalArgumentException();
  }

  public PureDupleBinaryWord(Set<Integer> s, int n) {
    super(s, n);
    if(!Numbers.isPowerOfTwo(n))
      throw new IllegalArgumentException();
  }
}
