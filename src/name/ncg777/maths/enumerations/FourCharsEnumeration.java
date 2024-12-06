package name.ncg777.maths.enumerations;

import java.util.Enumeration;

import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.words.Alphabet;
import name.ncg777.maths.words.FourChars;

public class FourCharsEnumeration implements Enumeration<FourChars>  {
  private Alphabet.Name alphabetName;
  
  private MixedRadixEnumeration mre;
  public FourCharsEnumeration(Alphabet.Name alphabetName) {
    this.alphabetName = alphabetName;
    var alphabet = Alphabet.getAlphabet(alphabetName);
    
    int n = alphabet.size();
    
    int[] base = {n,n,n,n};
    mre = new MixedRadixEnumeration(base);
  }
  
  @Override
  public boolean hasMoreElements() {
    return mre.hasMoreElements();
  }

  @Override
  public FourChars nextElement() {
    return new FourChars(alphabetName, new Sequence(mre.nextElement()));
  }
}