package name.ncg777.maths.words;

import static com.google.common.math.LongMath.checkedPow;

import java.util.ArrayList;
import java.util.TreeMap;

public class Alphabet extends ArrayList<Character> {
  private static final long serialVersionUID = 1L;

  public static enum Name {
    Hexadecimal,
    Octal,
    Binary
  }
  
  private static TreeMap<Name,Boolean> reversed = new TreeMap<>();
  public static boolean isStringReversed(Name name) { 
    return reversed.get(name); 
  }
  
  public static TreeMap<Name, Alphabet> Alphabets;
  
  static {
    Alphabets = new TreeMap<>();
    Character[] ARR_BINARY = {'0','1'};
    Character[] ARR_OCTAL = {'0','1','2','3','4','5','6','7'};
    Character[] ARR_HEXADECIMAL = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    
    Alphabets.put(Name.Binary, new Alphabet(ARR_BINARY));
    Alphabets.put(Name.Octal, new Alphabet(ARR_OCTAL));
    Alphabets.put(Name.Hexadecimal, new Alphabet(ARR_HEXADECIMAL));
    
    reversed.put(Name.Binary, true);
    reversed.put(Name.Octal, true);
    reversed.put(Name.Hexadecimal, true);
  }
  
  public double bitness() {
    return Math.log((double)this.size())/Math.log(2.0);
  }
  
  public boolean isBitnessANatural() {
    return checkedPow(Math.round(bitness()), 2) == size();
  }
  
  static public Alphabet getAlphabet(Name name) { return Alphabets.get(name); }
  
  public Alphabet(Character[] characters) {
    for(Character c : characters) {
      this.add(c);
    }
  }
  
  @Override
    public boolean equals(Object _other) {
      if(!(_other instanceof Alphabet)) return false;
      var other = (Alphabet) _other;
      if(this.size() != other.size()) return false;
      for(int i=0;i<this.size();i++) if(this.get(i) != other.get(i)) return false;
      return true;
    }
}
