package name.ncg777.maths.words;

import java.util.List;

import name.ncg777.maths.sequences.Sequence;

public class Tetragraph extends Word {
  private static final long serialVersionUID = 1L;

  public Tetragraph(Alphabet alphabet, Character[] array) {
    super(alphabet, array);
  }
  
  public Tetragraph(Alphabet alphabet, String string) {
    super(alphabet, string);
    if(string.length() != 4) throw new IllegalArgumentException();
  }
  
  public Tetragraph(Alphabet alphabet, List<Character> list) {
    super(alphabet, list);
  }
  
  public Tetragraph(Alphabet alphabet, Sequence sequence) {
    super(alphabet, sequence);
    if(sequence.size()!= 4) throw new IllegalArgumentException();
  }
  
  public Tetragraph(Digraph first, Digraph second) {
    this(first.getAlphabet(), first.toString()+second.toString());
    if(!first.getAlphabet().equals(second.getAlphabet())) throw new IllegalArgumentException();
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
