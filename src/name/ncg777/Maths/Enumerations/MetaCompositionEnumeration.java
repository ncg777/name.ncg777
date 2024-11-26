package name.ncg777.Maths.Enumerations;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import name.ncg777.Maths.Objects.Composition;
/**
 * https://oeis.org/A133494
 */
public class MetaCompositionEnumeration  implements Enumeration<String> {

  private String s;
  private CompositionEnumeration up;
  private Composition currentUp;
  
  private CompositionEnumeration low;
  private Composition currentLow;
  
  public MetaCompositionEnumeration(String s) {
    this.s = s;
    up = new CompositionEnumeration(s.length());
  }
  
  @Override
  public boolean hasMoreElements() {
    return !(!(low != null && low.hasMoreElements()) && !up.hasMoreElements());
  }

  @Override
  public String nextElement() {
    if(!hasMoreElements()) throw new NoSuchElementException();
    
    if(low == null || !low.hasMoreElements()) {
      currentUp = up.nextElement();
      low = new CompositionEnumeration(currentUp.getK()+1);
      currentLow = low.nextElement();
    } else {
      currentLow = low.nextElement();  
    }
    
    var l = currentLow.segmentList(currentUp.segmentString(s));
    
    var sb = new StringBuilder();
    
    for(int i=0; i<l.size();i++) {
      sb.append("<");
      for(int j=0; j<l.get(i).size(); j++) {
        sb.append("<");
        sb.append(l.get(i).get(j));
        sb.append(">");
      }
      sb.append(">");
    }
    
    return sb.toString()
        .replaceAll("><", "<")
        .replaceAll("><", ">")
        .replaceAll("<<", "<")
        .replaceAll(">>", ">");
  }
  
  public static ArrayList<String> extractStructure(String s) {
    if(s.charAt(0) != '<' || s.charAt(s.length()-1) != '>') 
      throw new IllegalArgumentException();
    
    var strs = new ArrayList<String>();
    var o = new ArrayList<String>();
    for(int i=0;i<s.length();i++) {
      switch(s.charAt(i)) {
        case '<':
          if(!strs.isEmpty()) o.add(strs.stream().reduce("", (a,b) -> a+b));
          break;
        case '>':
          o.add(strs.stream().reduce("", (a,b) -> a+b));
          strs.removeLast();
          break;
        default:
          strs.add(String.valueOf(s.charAt(i)));
      }
    }
    return o;
  }
}
