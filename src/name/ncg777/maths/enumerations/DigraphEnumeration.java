package name.ncg777.maths.enumerations;

import java.util.Enumeration;

import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.words.Alphabet;
import name.ncg777.maths.words.Digraph;

public class DigraphEnumeration implements Enumeration<Digraph>  {
  private Alphabet.Name alphabetName;
  private MixedRadixEnumeration mre;
  public DigraphEnumeration(Alphabet.Name alphabetName) {
    this.alphabetName = alphabetName;
    var alphabet = Alphabet.getAlphabet(alphabetName);
    int n = alphabet.size();
    
    int[] base = {n,n};
    mre = new MixedRadixEnumeration(base);
  }
  
  @Override
  public boolean hasMoreElements() {
    return mre.hasMoreElements();
  }

  @Override
  public Digraph nextElement() {
    return new Digraph(this.alphabetName, new Sequence(mre.nextElement()));
  }
}
