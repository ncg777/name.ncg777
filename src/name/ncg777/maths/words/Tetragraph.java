package name.ncg777.maths.words;

import java.util.List;
import java.util.TreeSet;

import name.ncg777.maths.enumerations.TetragraphEnumeration;
import name.ncg777.maths.sequences.Sequence;

public class Tetragraph extends Word {
  private static final long serialVersionUID = 1L;

  public Tetragraph(Alphabet.Name alphabetName, String[] array) {
    super(alphabetName, array);
  }
  
  public Tetragraph(Alphabet.Name alphabetName, String string) {
    super(alphabetName, string);
    if(string.length() != 4) throw new IllegalArgumentException();
  }
  
  public Tetragraph(Alphabet.Name alphabetName, List<String> list) {
    super(alphabetName, list);
  }
  
  public Tetragraph(Alphabet.Name alphabetName, Sequence sequence) {
    super(alphabetName, sequence);
    if(sequence.size()!= 4) throw new IllegalArgumentException();
  }
  public Tetragraph(Word word) {
    super(word);
    if(word.size()!= 4) throw new IllegalArgumentException();
  }
  public Tetragraph(Digraph first, Digraph second) {
    this(first.alphabetName, first.toString()+second.toString());
    if(!first.getAlphabet().equals(second.getAlphabet())) throw new IllegalArgumentException();
  }
  
  public static TreeSet<Tetragraph> generate(Alphabet.Name alphabetName) {
    TreeSet<Tetragraph> o = new TreeSet<Tetragraph>();
    var tge = new TetragraphEnumeration(alphabetName);
    while(tge.hasMoreElements()) o.add(tge.nextElement());
    return o;
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
  
  public int compareTo(Tetragraph o) {
    return this.toSequence().compareTo(o.toSequence());
  }
}
