package name.ncg777.Maths.Objects;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Parenthesization implements Comparable<Parenthesization> {
  public enum Parenthesis {
    OPEN,
    CLOSE
  }
  
  private static final char OPEN = '{';
  private static final char DEFAULT_CHAR = '|';
  private static final char CLOSE = '}';

  private Integer nbOfCharacters = null;
  private ArrayList<Character> innerParentheses = new ArrayList<>();
  private ArrayList<Character> characters = new ArrayList<Character>();
  
  public Parenthesization(int nbOfCharacters) {
    super();
    this.nbOfCharacters = nbOfCharacters;
    for(int i=0;i<nbOfCharacters-1;i++) innerParentheses.add(null);
    for(int i=0;i<nbOfCharacters;i++) characters.add(DEFAULT_CHAR);
  }
  
  public Parenthesization(String s) {
    super();
    this.nbOfCharacters = s.length();
    for(int i=0;i<this.nbOfCharacters-1;i++) innerParentheses.add(null);
    for(int i=0;i<this.nbOfCharacters;i++) characters.add(s.charAt(i));
  }
  
  public Parenthesization(Parenthesization p) {
    this(p.nbOfCharacters);
    characters.clear(); for(Character c : p.characters) characters.add(c);
    innerParentheses.clear(); for(Character c : p.innerParentheses) innerParentheses.add(c);
  }
  
  public Parenthesization mutateParenthesis(int i, Parenthesis p) {
    if(i < 0 || i > this.nbOfCharacters-1) throw new IndexOutOfBoundsException();
    Parenthesization c = new Parenthesization(this);
    if(p == Parenthesis.OPEN) c.innerParentheses.set(i, OPEN);
    if(p == null) c.innerParentheses.set(i, null);
    if(p == Parenthesis.CLOSE) c.innerParentheses.set(i, CLOSE);
    return c;
  }
  
  public Parenthesization mutateCharacter(int i, char c) {
    if(i < 0 || i > this.nbOfCharacters) throw new IndexOutOfBoundsException();
    Parenthesization p = new Parenthesization(this);
    p.characters.set(i,c);
    return p;
  }
  
  public static void enumerate(Consumer<String> consumer, int nbOfCharacters ) {
    enumerate(consumer, nbOfCharacters, new Parenthesization(nbOfCharacters), 0);
  }
  
  private static void enumerate(Consumer<String> consumer, int nbOfCharacters, Parenthesization current, int i) {
    consumer.accept(current.toString());
    if(i < nbOfCharacters-1) {
      enumerate(consumer, nbOfCharacters, current.mutateParenthesis(i, Parenthesis.OPEN), i+1);
      enumerate(consumer, nbOfCharacters, current.mutateParenthesis(i, null), i+1);
      enumerate(consumer, nbOfCharacters, current.mutateParenthesis(i, Parenthesis.CLOSE), i+1);
    }
  }
  
  public String toString() {
    return toString(false);
  }
  
  public String toString(boolean useDefaultChar) {
    int p_i = 0;
    int c_i = 0;
    
    StringBuilder sb = new StringBuilder();
    sb.append(OPEN);
    while(c_i < characters.size()) {
      c_i++;
      sb.append(useDefaultChar ? DEFAULT_CHAR : characters.get(c_i-1));
      
      if(p_i++ < innerParentheses.size() && innerParentheses.get(p_i-1) != null) {
          sb.append(innerParentheses.get(p_i-1));  
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
