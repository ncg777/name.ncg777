package name.ncg777.maths.numbers;

import static com.google.common.math.LongMath.checkedPow;

import java.util.ArrayList;
import java.util.TreeMap;

public class Cipher extends ArrayList<Character> {
  private static final long serialVersionUID = 1L;

  public static enum Name {
    Hexadecimal,
    Decimal,
    Octal,
    Binary,
    Ternary
  }
  
  public static TreeMap<Name, Cipher> Ciphers;
  
  static {
    Ciphers = new TreeMap<>();
    Character[] ARR_TERNARY = {'T','0','1'};
    Character[] ARR_BINARY = {'0','1'};
    Character[] ARR_OCTAL = {'0','1','2','3','4','5','6','7'};
    Character[] ARR_DECIMAL = {'0','1','2','3','4','5','6','7','8','9'};
    Character[] ARR_HEXADECIMAL = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    
    Ciphers.put(Name.Hexadecimal, new Cipher(ARR_HEXADECIMAL));
    Ciphers.put(Name.Decimal, new Cipher(ARR_DECIMAL));
    Ciphers.put(Name.Octal, new Cipher(ARR_OCTAL));
    Ciphers.put(Name.Binary, new Cipher(ARR_BINARY));
    Ciphers.put(Name.Ternary, new Cipher(ARR_TERNARY));
  }
  
  public double information() {
    return Math.log((double)this.size())/Math.log(2.0);
  }
 
  public boolean isInformationBinary() {
    return checkedPow(2, (int)Math.round(information())) == size();
  }
  
  static public Cipher getAlphabet(Name name) { return Ciphers.get(name); }
  
  public Cipher(Character[] characters) {
    for(Character c : characters) {
      this.add(c);
    }
  }
  
  @Override
  public boolean equals(Object _other) {
    if(!(_other instanceof Cipher)) return false;
    var other = (Cipher) _other;
    if(this.size() != other.size()) return false;
    for(int i=0;i<this.size();i++) if(this.get(i) != other.get(i)) return false;
    return true;
  }
}
