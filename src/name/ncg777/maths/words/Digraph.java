package name.ncg777.maths.words;

import name.ncg777.maths.sequences.Sequence;
import java.util.List;

public class Digraph extends Word {
  private static final long serialVersionUID = 1L;

  public Digraph(Alphabet alphabet, String[] array) {
    super(alphabet, array);
  }
  
  public Digraph(Alphabet alphabet, String string) {
    super(alphabet, string);
    if(string.length() != 2) throw new IllegalArgumentException();
  }
  
  public Digraph(Alphabet alphabet, List<String> list) {
    super(alphabet, list);
  }
  
  public Digraph(Alphabet alphabet, Sequence sequence) {
    super(alphabet, sequence);
    if(sequence.size()!= 2) throw new IllegalArgumentException();
  }
}
