package name.ncg777.maths.words;

import static com.google.common.math.LongMath.checkedPow;

import java.util.ArrayList;
import java.util.TreeMap;

public class Alphabet extends ArrayList<Character> {
  private static final long serialVersionUID = 1L;

  public static enum Names {
    Binary,
    Octal,
    Hexadecimal
  }
  
  private static Character[] ARR_BINARY = {'0','1'};
  private static Character[] ARR_OCTAL = {'0','1','2','3','4','5','6','7'};
  private static Character[] ARR_HEXADECIMAL = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
  
  public static Alphabet Binary = new Alphabet(ARR_BINARY);
  public static Alphabet Octal = new Alphabet(ARR_OCTAL);
  public static Alphabet Hexadecimal = new Alphabet(ARR_HEXADECIMAL);
  
  public static TreeMap<Names, Alphabet> Alphabets;
  
  static {
    Alphabets = new TreeMap<>();
    Alphabets.put(Names.Binary, Binary);
    Alphabets.put(Names.Octal, Octal);
    Alphabets.put(Names.Hexadecimal, Hexadecimal);
  }
  
  public double bitness() {
    return Math.log((double)this.size())/Math.log(2.0);
  }
  
  public boolean isBitnessANatural() {
    return checkedPow(Math.round(bitness()), 2) == size();
  }
  
  static public Alphabet getAlphabet(Names name) { return Alphabets.get(name); }
  
  public Alphabet(Character[] characters) {
    for(Character c : characters) this.add(c);
  }
  
  public Word toWord() {
    return new Word(this, this);
  }

  @Override
    public boolean equals(Object _other) {
      if(!(_other instanceof Alphabet)) return false;
      var other = (Word) _other;
      if(this.size() != other.size()) return false;
      for(int i=0;i<this.size();i++) if(this.get(i) != other.get(i)) return false;
      return true;
    }
}
