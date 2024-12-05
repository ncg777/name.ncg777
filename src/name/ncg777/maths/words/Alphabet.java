package name.ncg777.maths.words;

import static com.google.common.math.LongMath.checkedPow;

import java.util.ArrayList;
import java.util.TreeMap;

public class Alphabet extends ArrayList<String> {
  private static final long serialVersionUID = 1L;

  public static enum Name {
    //Tribble,
    Hexadecimal,
    Octal,
    Binary
  }
  
  private static TreeMap<Name,Boolean> reversed = new TreeMap<>();
  public static boolean isStringReversed(Name name) { 
    return reversed.get(name); 
  }
  public static Alphabet Binary;
  public static Alphabet Octal;
  public static Alphabet Hexadecimal;
  //public static Alphabet Tribble;
  
  public static TreeMap<Name, Alphabet> Alphabets;
  
  static {
    Alphabets = new TreeMap<>();
    String[] ARR_BINARY = {"0","1"};
    String[] ARR_OCTAL = {"0","1","2","3","4","5","6","7"};
    String[] ARR_HEXADECIMAL = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
    //String[] ARR_TRIBBLE = new String[4096];
    
    // CJK Unified Ideographs Extension C for the win.
    //for (int i = 0; i < 4096; i++) {
    //  ARR_TRIBBLE[i] = Character.toString(0x2A700+i);
    //}
    
    Binary = new Alphabet(ARR_BINARY);
    Octal = new Alphabet(ARR_OCTAL);
    Hexadecimal = new Alphabet(ARR_HEXADECIMAL);
    //Tribble = new Alphabet(ARR_TRIBBLE);
    
    Alphabets.put(Name.Binary, Binary);
    Alphabets.put(Name.Octal, Octal);
    Alphabets.put(Name.Hexadecimal, Hexadecimal);
    
    reversed.put(Name.Binary, true);
    reversed.put(Name.Octal, true);
    reversed.put(Name.Hexadecimal, true);
    //Alphabets.put(Names.Tribble, Tribble);
  }
  
  public double bitness() {
    return Math.log((double)this.size())/Math.log(2.0);
  }
  
  public boolean isBitnessANatural() {
    return checkedPow(Math.round(bitness()), 2) == size();
  }
  
  static public Alphabet getAlphabet(Name name) { return Alphabets.get(name); }
  
  public Alphabet(String[] characters) {
    for(String s : characters) {
      if(s.length() != 1) throw new IllegalArgumentException();
      this.add(s);
    }
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
