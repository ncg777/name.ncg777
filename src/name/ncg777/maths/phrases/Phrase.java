package name.ncg777.maths.phrases;

import java.util.ArrayList;

import name.ncg777.maths.words.Alphabet;
import name.ncg777.maths.words.Word;

public class Phrase<T extends Word> extends ArrayList<T> {
  private static final long serialVersionUID = 1L;
  private Alphabet.Name alphabetName;
  public Alphabet getAlphabet() { return Alphabet.getAlphabet(alphabetName); }
  public Phrase(Alphabet.Name alphabetName) {
    this.alphabetName = alphabetName;
  }
}
