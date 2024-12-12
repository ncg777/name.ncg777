package name.ncg777.maths.words;

import java.util.List;
import java.util.TreeSet;

import name.ncg777.maths.enumerations.QuartalWordEnumeration;
import name.ncg777.maths.sequences.Sequence;

public class QuartalWord extends Word {
  private static final long serialVersionUID = 1L;

  public QuartalWord(Alphabet.Name alphabetName, Character[] array) {
    super(alphabetName, array);
  }
  
  public QuartalWord(Alphabet.Name alphabetName, String string) {
    super(alphabetName, string.replaceAll("\\s+", ""));
    if(string.replaceAll("\\s+", "").length() != 4) throw new IllegalArgumentException();
  }
  
  public QuartalWord(Alphabet.Name alphabetName, List<Character> list) {
    super(alphabetName, list);
  }
  
  public QuartalWord(Alphabet.Name alphabetName, Sequence sequence) {
    super(alphabetName, sequence);
    if(sequence.size()!= 4) throw new IllegalArgumentException();
  }
 
  public QuartalWord(Word word) {
    super(word);
    if(word.size()!= 4) throw new IllegalArgumentException();
  }
    
  public static TreeSet<QuartalWord> generate(Alphabet.Name alphabetName) {
    TreeSet<QuartalWord> o = new TreeSet<QuartalWord>();
    var tge = new QuartalWordEnumeration(alphabetName);
    while(tge.hasMoreElements()) o.add(tge.nextElement());
    return o;
  }
  
  @Override
  public String toString() {
    return this.toString(true);
  }
  
  public String toString(boolean insertSpace) {
    StringBuilder sb = new StringBuilder();
    sb.append(this.get(3));
    sb.append(this.get(2));
    if(insertSpace) sb.append(" ");
    sb.append(this.get(1));
    sb.append(this.get(0));
    
    return sb.toString();
  }
  
  public int compareTo(QuartalWord o) {
    return this.toSequence().compareTo(o.toSequence());
  }
}
