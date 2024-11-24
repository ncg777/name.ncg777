package name.ncg777.Maths.Enumerations;

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
      sb.append("[");
      for(int j=0; j<l.get(i).size(); j++) {
        sb.append("[");
        sb.append(l.get(i).get(j));
        sb.append("]");
      }
      sb.append("]");
    }
    
    return sb.toString();
  }
}
