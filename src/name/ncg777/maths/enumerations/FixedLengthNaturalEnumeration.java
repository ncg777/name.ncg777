package name.ncg777.maths.enumerations;

import java.util.Enumeration;

import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.fixed.FixedLength;
import name.ncg777.maths.sequences.Sequence;

public class FixedLengthNaturalEnumeration implements Enumeration<FixedLength.Natural>  {
  private Cipher.Name alphabetName;
  private MixedRadixEnumeration mre;
  private int L;
  public FixedLengthNaturalEnumeration(int L, Cipher.Name alphabetName) {
    this.alphabetName = alphabetName;
    this.L = L;
    var alphabet = Cipher.getAlphabet(alphabetName);
    
    int n = alphabet.size();
    
    int[] base = new int[L];
    for(int i=0;i<L;i++) base[i]=n;

    mre = new MixedRadixEnumeration(base);
  }
  
  @Override
  public boolean hasMoreElements() {
    return mre.hasMoreElements();
  }

  @Override
  public FixedLength.Natural nextElement() {
    try {
      return FixedLength.newNatural(this.L,alphabetName, new Sequence(mre.nextElement()));
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}