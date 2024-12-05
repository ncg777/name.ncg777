package name.ncg777.maths.enumerations;

import java.util.Enumeration;

import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.words.Alphabet;
import name.ncg777.maths.words.Tetragraph;

public class TetragraphEnumeration implements Enumeration<Tetragraph>  {
  private Alphabet.Name alphabetName;
  
  private MixedRadixEnumeration mre;
  public TetragraphEnumeration(Alphabet.Name alphabetName) {
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
  public Tetragraph nextElement() {
    return new Tetragraph(alphabetName, new Sequence(mre.nextElement()));
  }
}