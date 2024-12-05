package name.ncg777.maths.words;

import name.ncg777.maths.sequences.Sequence;
import java.util.List;

public class Digraph extends Word {
  private static final long serialVersionUID = 1L;

  public Digraph(Character[] array) {
    super(array);
  }
  
  public Digraph(String string) {
    super(string);
    if(string.length() != 2) throw new IllegalArgumentException();
  }
  
  public Digraph(List<Character> list) {
    super(list);
  }
  
  public Digraph(Sequence sequence, Alphabet alphabet) {
    super(sequence, alphabet);
    if(sequence.size()!= 2) throw new IllegalArgumentException();
  }
}
