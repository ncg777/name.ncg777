package name.ncg777.maths.sentences;

import java.util.ArrayList;

import name.ncg777.maths.words.Alphabet;
import name.ncg777.maths.words.Word;

public class Sentence<T extends Word> extends ArrayList<T> {
  private static final long serialVersionUID = 1L;
  private Alphabet alphabet;
  public Alphabet getAlphabet() { return alphabet; }
  public Sentence(Alphabet alphabet) {
    this.alphabet = alphabet;
  }
}
