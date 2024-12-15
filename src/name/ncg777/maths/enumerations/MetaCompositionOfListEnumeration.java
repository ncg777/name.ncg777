package name.ncg777.maths.enumerations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.collect.ComparisonChain;

public class MetaCompositionOfListEnumeration  implements Enumeration<String> {
  private MetaCompositionEnumeration mce;
  
  private TreeMap<String, Character> characters = new TreeMap<String, Character>();
  private TreeMap<Character, String> words = new TreeMap<Character, String>();
  
  public MetaCompositionOfListEnumeration(String s) {
    this(s, false);
  }
  
  private String wordsToChars(String str) {
    var ws = new ArrayList<String>();
    for(var w :  characters.keySet()) ws.add(w);
    
    ws.sort(
        (a,b) -> ComparisonChain.start().compare(
            b.length(), 
            a.length()
        )
        .compare(a, b).result());
    
    for(var w : ws) {
      str = str.replace(w.subSequence(0, w.length()),  characters.get(w).toString().subSequence(0, 1));
    }
    return str.replace(" ", "");
  }
  
  private String charsToWords(String str) { 
    var chars = new ArrayList<Character>();
    chars.addAll(words.keySet());
    
    for(var c : chars) {
      var w = words.get(c);
      str = str.replace(c.toString().subSequence(0, 1), (w + " ").subSequence(0, w.length()+1));
    }
    return str.trim().replace(" <", "<").replace(" >", ">").replace(" ", "-").replace("--", " ");
  }
  
  public MetaCompositionOfListEnumeration(String str, boolean transform) {
    var s = Arrays.asList(str.split("\\s+"));
    TreeSet<String> distinct = new TreeSet<String>();
    distinct.addAll(s);
    char k = (char)63; // starting after < and > to avoid these reserved chars
    for(var w : distinct) {
      // Avoiding characters already in the string for easy going substitution
      do{k++;}while(str.contains(Character.valueOf(k).toString()));
      characters.put(w,Character.valueOf(k));
      words.put(Character.valueOf(k),w);
    }
    mce = new MetaCompositionEnumeration(wordsToChars(str), transform);
  }
  
  @Override
  public boolean hasMoreElements() {
    return mce.hasMoreElements();
  }

  @Override
  public String nextElement() {
    if(!hasMoreElements()) throw new NoSuchElementException();
    return charsToWords(mce.nextElement());
  }
}