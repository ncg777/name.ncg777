package name.ncg777.Maths.Objects;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;


/**
 * 
 * P(n) = \sum_{k=0}^{n} \binom{n}{k} \cdot 2^{n-k}
 */
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
    this.nbOfCharacters = nbOfCharacters;
    innerParentheses = new ArrayList<>();
    
    for(int i=0;i<this.nbOfCharacters-1;i++) innerParentheses.add(null);
    
    characters = new ArrayList<Character>();
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
    else innerParentheses.set(i, OPEN);
    return c;
  }
  
  public void setCharacter(int i, char c) {
    if(i < 0 || i > this.nbOfCharacters) throw new IndexOutOfBoundsException();
    this.characters.set(i,c);
  }
  
  public static Set<Parenthesization> generate(int nbOfCharacters) {
    return generate(new TreeSet<Parenthesization>(), nbOfCharacters, new Parenthesization(nbOfCharacters), 0);
  }
  
  private static Set<Parenthesization> generate(Set<Parenthesization> set, int nbOfCharacters, Parenthesization current, int i) {
    set.add(current);
    if(i < nbOfCharacters-1) {
      try {
        generate(set,nbOfCharacters, ((Parenthesization)current.clone()).mutateParenthesis(i, Parenthesis.OPEN), i+1);
        generate(set,nbOfCharacters, ((Parenthesization)current.clone()).mutateParenthesis(i, Parenthesis.CLOSE), i+1);
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
    
    while(character_index < this.nbOfCharacters) {
      if(b) {
        sb.append(innerParentheses.get(parenthesis_index++));
      } else {
        sb.append(useDefaultCharacter ? DEFAULT_CHAR : characters.get(character_index++));
      }
      
      if(parenthesis_index < this.innerParentheses.size() && 
          innerParentheses.get(parenthesis_index) == null) {
        b = !b;
      }
    }
    
    return OPEN + sb.toString() + CLOSE;
  }

  @Override
  public int compareTo(Parenthesization o) {
    return this.toString(true).compareTo(o.toString(true));
  }
  
}
