package name.ncg777.mathematics.enumerations;

import java.util.Enumeration;

public class WordEnumeration implements Enumeration<int[]>{
  private MixedRadixEnumeration mre;
  
  public WordEnumeration(int length, int size) {
    int[] base = new int[length];
    for(int i=0;i<length;i++){
      base[i] = size;
    }
    mre = new MixedRadixEnumeration(base);
  }
  @Override
  public boolean hasMoreElements() {
    return mre.hasMoreElements();
  }

  @Override
  public int[] nextElement() {
    return mre.nextElement();
  }
}
