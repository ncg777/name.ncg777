package name.ncg777.Maths.Objects;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class Parenthesization implements Comparable<Parenthesization> {
  public enum Parenthesis {
    OPEN,
    CLOSE
  }
  
  private static final char OPEN = '{';
  private static final char DEFAULT_CHAR = '|';
  private static final char CLOSE = '}';

  private Integer nbOfCharacters = null;
  private ArrayList<Character> innerParentheses = null;
  private ArrayList<Character> characters = null;
  
  public Parenthesization(int nbOfCharacters) {
    super();
    this.nbOfCharacters = nbOfCharacters;
    innerParentheses = new ArrayList<>();
    for(int i=0;i<nbOfCharacters-1;i++) innerParentheses.add(null);
    
    this.characters = new ArrayList<Character>();
    for(int i=0;i<nbOfCharacters;i++) characters.add(DEFAULT_CHAR);
  }
  
  public Parenthesization(String s) {
    super();
    this.nbOfCharacters = s.length();
    innerParentheses = new ArrayList<>();
    
    for(int i=0;i<this.nbOfCharacters-1;i++) innerParentheses.add(null);
    
    this.characters = new ArrayList<Character>();
    for(int i=0;i<s.length();i++) characters.add(s.charAt(i));
    for(int i=0;i<this.nbOfCharacters;i++) characters.add(i, DEFAULT_CHAR);
  }
  
  @SuppressWarnings("null")
  public Parenthesization mutateParenthesis(int i, Parenthesis p) {
    if(i < 0 || i > this.nbOfCharacters-1) throw new IndexOutOfBoundsException();
    
    Parenthesization c = null;
    try {
      c = (Parenthesization)this.clone();
    } catch (CloneNotSupportedException e){;}
    if(p == Parenthesis.CLOSE) c.innerParentheses.set(i, CLOSE);
    if(p == null) c.innerParentheses.set(i, null);
    if(p == Parenthesis.OPEN) c.innerParentheses.set(i, OPEN);
    return c;
  }
  
  public Parenthesization mutateCharacter(int i, char c) {
    if(i < 0 || i > this.nbOfCharacters) throw new IndexOutOfBoundsException();
    Parenthesization p = null;
    try {
      p = ((Parenthesization)this.clone());
      p.characters.set(i,c);
    } catch(CloneNotSupportedException ex) {;}
    
    return p;
  }
  
  public static Set<Parenthesization> generate(int nbOfCharacters) {
    return generate(new TreeSet<Parenthesization>(), nbOfCharacters, new Parenthesization(nbOfCharacters), 0);
  }
  
  private static Set<Parenthesization> generate(Set<Parenthesization> set, int nbOfCharacters, Parenthesization current, int i) {
    set.add(current);
    if(i < nbOfCharacters-1) {
      try {
        generate(set, nbOfCharacters, ((Parenthesization)current.clone()).mutateParenthesis(i, Parenthesis.OPEN), i+1);
        generate(set, nbOfCharacters, ((Parenthesization)current.clone()).mutateParenthesis(i, null), i+1);
        generate(set, nbOfCharacters, ((Parenthesization)current.clone()).mutateParenthesis(i, Parenthesis.CLOSE), i+1);
      } catch (CloneNotSupportedException e){;}
    }
    return set;
  }
  
  public String toString() {
    return this.toString(false);
  }
  
  public String toString(boolean useDefaultCharacter) {
    int parenthesis_index = 0;
    int character_index = 0;
    
    boolean b = false;
    StringBuilder sb = new StringBuilder();
    sb.append(OPEN);
    while(parenthesis_index < this.nbOfCharacters-1) {
      if(b) {
        if(innerParentheses.get(parenthesis_index++) != null) {
          sb.append(innerParentheses.get(parenthesis_index));  
        }
      } else {
        sb.append(useDefaultCharacter ? DEFAULT_CHAR : characters.get(character_index++));
      }
      
      if(parenthesis_index < this.innerParentheses.size() && 
          innerParentheses.get(parenthesis_index) == null) {
        b = !b;
      }
    }
    sb.append(CLOSE);
    return sb.toString();
  }

  @Override
  public int compareTo(Parenthesization o) {
    return this.toString(true).compareTo(o.toString(true));
  }
}
