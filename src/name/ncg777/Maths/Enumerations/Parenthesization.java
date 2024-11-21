package name.ncg777.Maths.Enumerations;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * 
 * P(n) = \sum_{k=0}^{n} \binom{n}{k} \cdot 2^{n-k}
 */
public class Parenthesization implements Enumeration<String> {

  private static final char OPEN = '(';
  private static final char CHAR = '□';
  private static final char CLOSE = ')';
  
  private CombinationEnumeration ce = null;
  
  private BitSetEnumeration be = null;
  private int n=1;
  private int k=1;
  public Parenthesization(int n, int k) {
    if(n < 1 || k < 1) throw new IllegalArgumentException();
    this.n = n;
    this.k = k;
    ce = new CombinationEnumeration(n,k);
    be = new BitSetEnumeration(n-k);
  }
  @Override
  public boolean hasMoreElements() {
    return (k <= n);
  }

  @Override
  public String nextElement() {
    if(!hasMoreElements()) throw new NoSuchElementException();
    
    var c = ce.nextElement().asBinarySequence();
    var b = be.nextElement();
    
    var sb = new StringBuilder();
    
    int i = -1;
    
    while(++i < n) {
      if(c.get(i).equals(1)) {
        sb.append(CHAR);
      } else {
        if(b.get(i)) {
          sb.append(OPEN);
        } else {
          sb.append(CLOSE);
        }
      }
    }
    
    if(!be.hasMoreElements()) {
      if(!ce.hasMoreElements()) {
        ce = new CombinationEnumeration(n,k);
      } 
      be = new BitSetEnumeration(n-k);
    }
    
    return sb.toString();
  }
}
