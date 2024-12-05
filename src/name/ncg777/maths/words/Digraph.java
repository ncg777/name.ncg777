package name.ncg777.maths.words;

import name.ncg777.maths.sequences.Sequence;
import java.util.List;

public class Digraph extends Word {
  private static final long serialVersionUID = 1L;

  public Digraph(Alphabet.Name alphabetName, Character[] array) {
    super(alphabetName, array);
  }
  
  public Digraph(Alphabet.Name alphabetName, String string) {
    super(alphabetName, string);
    if(string.length() != 2) throw new IllegalArgumentException();
  }
  
  public Digraph(Alphabet.Name alphabetName, List<Character> list) {
    super(alphabetName, list);
  }
  
  public Digraph(Alphabet.Name alphabetName, Sequence sequence) {
    super(alphabetName, sequence);
    if(sequence.size()!= 2) throw new IllegalArgumentException();
  }
}
