package name.ncg777.maths.words;

import java.util.List;
import java.util.TreeSet;

import name.ncg777.maths.enumerations.TetragraphEnumeration;
import name.ncg777.maths.sequences.Sequence;

public class FourChars extends Word {
  private static final long serialVersionUID = 1L;

  public FourChars(Alphabet.Name alphabetName, Character[] array) {
    super(alphabetName, array);
  }
  
  public FourChars(Alphabet.Name alphabetName, String string) {
    super(alphabetName, string);
    if(string.length() != 4) throw new IllegalArgumentException();
  }
  
  public FourChars(Alphabet.Name alphabetName, List<Character> list) {
    super(alphabetName, list);
  }
  
  public FourChars(Alphabet.Name alphabetName, Sequence sequence) {
    super(alphabetName, sequence);
    if(sequence.size()!= 4) throw new IllegalArgumentException();
  }
 
  public FourChars(Word word) {
    super(word);
    if(word.size()!= 4) throw new IllegalArgumentException();
  }
  
  public FourChars(Digraph first, Digraph second) {
    this(first.alphabetName, first.toString()+second.toString());
    if(!first.getAlphabet().equals(second.getAlphabet())) throw new IllegalArgumentException();
  }
  
  public static TreeSet<FourChars> generate(Alphabet.Name alphabetName) {
    TreeSet<FourChars> o = new TreeSet<FourChars>();
    var tge = new TetragraphEnumeration(alphabetName);
    while(tge.hasMoreElements()) o.add(tge.nextElement());
    return o;
  }
  
  @Override
  public String toString() {
    return this.toString(true);
  }
  
  public String toString(boolean as2Digraphs) {
    StringBuilder sb = new StringBuilder();
    sb.append(this.get(3));
    sb.append(this.get(2));
    if(as2Digraphs) sb.append(" ");
    sb.append(this.get(1));
    sb.append(this.get(0));
    
    return sb.toString();
  }
  
  public int compareTo(FourChars o) {
    return this.toSequence().compareTo(o.toSequence());
  }
}
