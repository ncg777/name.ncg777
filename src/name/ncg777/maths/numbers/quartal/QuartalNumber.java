package name.ncg777.maths.numbers.quartal;

import java.util.List;
import java.util.TreeSet;

import name.ncg777.maths.enumerations.QuartalNumberEnumeration;
import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.Natural;
import name.ncg777.maths.sequences.Sequence;

public class QuartalNumber extends Natural {
  private static final long serialVersionUID = 1L;

  public QuartalNumber(Cipher.Name alphabetName, Character[] array) {
    super(alphabetName, array);
  }
  
  public QuartalNumber(Cipher.Name alphabetName, String string) {
    super(alphabetName, string.replaceAll("\\s+", ""));
    if(string.replaceAll("\\s+", "").length() != 4) throw new IllegalArgumentException();
  }
  
  public QuartalNumber(Cipher.Name alphabetName, List<Character> list) {
    super(alphabetName, list);
  }
  
  public QuartalNumber(Cipher.Name alphabetName, Sequence sequence) {
    super(alphabetName, sequence);
    if(sequence.size()!= 4) throw new IllegalArgumentException();
  }
 
  public QuartalNumber(Natural natural) {
    super(natural);
    if(natural.size()!= 4) throw new IllegalArgumentException();
  }
    
  public static TreeSet<QuartalNumber> generate(Cipher.Name alphabetName) {
    TreeSet<QuartalNumber> o = new TreeSet<QuartalNumber>();
    var tge = new QuartalNumberEnumeration(alphabetName);
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
  
  public int compareTo(QuartalNumber o) {
    return this.toSequence().compareTo(o.toSequence());
  }
}
