package name.ncg777.maths.enumerations;

import java.util.Enumeration;

import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.QuartalNumber;
import name.ncg777.maths.sequences.Sequence;

public class QuartalWordEnumeration implements Enumeration<QuartalNumber>  {
  private Cipher.Name alphabetName;
  
  private MixedRadixEnumeration mre;
  public QuartalWordEnumeration(Cipher.Name alphabetName) {
    this.alphabetName = alphabetName;
    var alphabet = Cipher.getAlphabet(alphabetName);
    
    int n = alphabet.size();
    
    int[] base = {n,n,n,n};
    mre = new MixedRadixEnumeration(base);
  }
  
  @Override
  public boolean hasMoreElements() {
    return mre.hasMoreElements();
  }

  @Override
  public QuartalNumber nextElement() {
    return new QuartalNumber(alphabetName, new Sequence(mre.nextElement()));
  }
}