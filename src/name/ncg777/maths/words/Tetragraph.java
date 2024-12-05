package name.ncg777.maths.words;

import java.util.List;

import name.ncg777.maths.sequences.Sequence;

public class Tetragraph extends Word {
  private static final long serialVersionUID = 1L;

  public Tetragraph(Character[] array) {
    super(array);
  }
  
  public Tetragraph(String string) {
    super(string);
    if(string.length() != 4) throw new IllegalArgumentException();
  }
  
  public Tetragraph(List<Character> list) {
    super(list);
  }
  
  public Tetragraph(Sequence sequence, Alphabet alphabet) {
    super(sequence, alphabet);
    if(sequence.size()!= 4) throw new IllegalArgumentException();
  }
  
  public Tetragraph(Digraph first, Digraph second) {
    this(first.toString()+second.toString());
  }
  
  @Override
  public String toString() {
    return this.toString(false);
  }
  
  public String toString(boolean as2Digraphs) {
    StringBuilder sb = new StringBuilder();
    sb.append(this.get(0));
    sb.append(this.get(1));
    if(as2Digraphs) sb.append(" ");
    sb.append(this.get(2));
    sb.append(this.get(3));
    return sb.toString();
  }
}
