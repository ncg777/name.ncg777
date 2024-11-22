package name.ncg777.Maths.Objects;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * 
 * P(n) = \sum_{k=0}^{n} \binom{n}{k} \cdot 2^{n-k}
 */
public class Parenthesization {
  public enum Parenthesis {
    OPEN,
    CLOSE
  }
  
  private static final char OPEN = '(';
  private static final char DEFAULT_CHAR = '□';
  private static final char CLOSE = ')';

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
  
  public void setParenthesis(int i, Parenthesis p) {
    if(i < 0 || i > this.nbOfCharacters-1) throw new IndexOutOfBoundsException();
    if(p == Parenthesis.CLOSE) innerParentheses.set(i, CLOSE);
    else innerParentheses.set(i, OPEN);
  }
  public void setCharacter(int i, char c) {
    if(i < 0 || i > this.nbOfCharacters) throw new IndexOutOfBoundsException();
    this.characters.set(i,c);
  }
  /*
  public TreeSet<Parenthesis> generate() {
    
  }
  */
  public String toString() {
    int parenthesis_index = 0;
    int character_index = 0;
    
    boolean b = false;
    
    StringBuilder sb = new StringBuilder();
    
    while(character_index < this.nbOfCharacters) {
      if(b) {
        sb.append(innerParentheses.get(parenthesis_index++));
      } else {
        sb.append(characters.get(character_index++));
      }
      
      if(parenthesis_index < this.innerParentheses.size() && 
          innerParentheses.get(parenthesis_index) == null) {
        b = !b;
      }
    }
    
    return OPEN + sb.toString() + CLOSE;
  }
  
}
