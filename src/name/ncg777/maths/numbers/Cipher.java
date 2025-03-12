package name.ncg777.maths.numbers;

import static com.google.common.math.LongMath.checkedPow;

import java.util.ArrayList;
import java.util.TreeMap;

public class Cipher extends ArrayList<Character> {
  private static final long serialVersionUID = 1L;

  public static enum Name {
    Hexadecimal,
    Octal,
    Binary,
    //LatinExtAFF
    //Ternary
    //Decimal
  }
  
  public static TreeMap<Name, Cipher> Ciphers;
  
  static {
    Ciphers = new TreeMap<>();
    //Character[] ARR_TERNARY = {'T','0','1'};
    Character[] ARR_BINARY = {'0','1'};
    Character[] ARR_OCTAL = {'0','1','2','3','4','5','6','7'};
    //Character[] ARR_DECIMAL = {'0','1','2','3','4','5','6','7','8','9'};
    Character[] ARR_HEXADECIMAL = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    //Character[] ARR_LATINEXTAFF = {'Ā','ā','Ă','ă','Ą','ą','Ć','ć','Ĉ','ĉ','Ċ','ċ','Č','č','Ď','ď','Đ','đ','Ē','ē','Ĕ','ĕ','Ė','ė','Ę','ę','Ě','ě','Ĝ','ĝ','Ğ','ğ','Ġ','ġ','Ģ','ģ','Ĥ','ĥ','Ħ','ħ','Ĩ','ĩ','Ī','ī','Ĭ','ĭ','Į','į','İ','ı','Ĳ','ĳ','Ĵ','ĵ','Ķ','ķ','ĸ','Ĺ','ĺ','Ļ','ļ','Ľ','ľ','Ŀ','ŀ','Ł','ł','Ń','ń','Ņ','ņ','Ň','ň','ŉ','Ŋ','ŋ','Ō','ō','Ŏ','ŏ','Ő','ő','Œ','œ','Ŕ','ŕ','Ŗ','ŗ','Ř','ř','Ś','ś','Ŝ','ŝ','Ş','ş','Š','š','Ţ','ţ','Ť','ť','Ŧ','ŧ','Ũ','ũ','Ū','ū','Ŭ','ŭ','Ů','ů','Ű','ű','Ų','ų','Ŵ','ŵ','Ŷ','ŷ','Ÿ','Ź','ź','Ż','ż','Ž','ž','ſ','ƀ','Ɓ','Ƃ','ƃ','Ƅ','ƅ','Ɔ','Ƈ','ƈ','Ɖ','Ɗ','Ƌ','ƌ','ƍ','Ǝ','Ə','Ɛ','Ƒ','ƒ','Ɠ','Ɣ','ƕ','Ɩ','Ɨ','Ƙ','ƙ','ƚ','ƛ','Ɯ','Ɲ','ƞ','Ɵ','Ơ','ơ','Ƣ','ƣ','Ƥ','ƥ','Ʀ','Ƨ','ƨ','Ʃ','ƪ','ƫ','Ƭ','ƭ','Ʈ','Ư','ư','Ʊ','Ʋ','Ƴ','ƴ','Ƶ','ƶ','Ʒ','Ƹ','ƹ','ƺ','ƻ','Ƽ','ƽ','ƾ','ƿ','ǀ','ǁ','ǂ','ǃ','Ǆ','ǅ','ǆ','Ǉ','ǈ','ǉ','Ǌ','ǋ','ǌ','Ǎ','ǎ','Ǐ','ǐ','Ǒ','ǒ','Ǔ','ǔ','Ǖ','ǖ','Ǘ','ǘ','Ǚ','ǚ','Ǜ','ǜ','ǝ','Ǟ','ǟ','Ǡ','ǡ','Ǣ','ǣ','Ǥ','ǥ','Ǧ','ǧ','Ǩ','ǩ','Ǫ','ǫ','Ǭ','ǭ','Ǯ','ǯ','ǰ','Ǳ','ǲ','ǳ','Ǵ','ǵ','Ƕ','Ƿ','Ǹ','ǹ','Ǻ','ǻ','Ǽ','ǽ','Ǿ','ǿ'};
    Ciphers.put(Name.Hexadecimal, new Cipher(ARR_HEXADECIMAL));
    Ciphers.put(Name.Octal, new Cipher(ARR_OCTAL));
    Ciphers.put(Name.Binary, new Cipher(ARR_BINARY));
    //Ciphers.put(Name.Ternary, new Cipher(ARR_TERNARY));
    //Ciphers.put(Name.Decimal, new Cipher(ARR_DECIMAL));
    //Ciphers.put(Name.LatinExtAFF, new Cipher(ARR_LATINEXTAFF));
  }
  
  public double information() {
    return Math.log((double)this.size())/Math.log(2.0);
  }
 
  public boolean isInformationBinary() {
    return checkedPow(2, (int)Math.round(information())) == size();
  }
  
  static public Cipher getCipher(Name name) { return Ciphers.get(name); }
  
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
