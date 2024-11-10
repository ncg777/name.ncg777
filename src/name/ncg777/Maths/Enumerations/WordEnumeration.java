package name.ncg777.Maths.Enumerations;

import java.util.Enumeration;

public class WordEnumeration implements Enumeration<Integer[]>{

  private MixedRadixEnumeration mre;
  
  public WordEnumeration(Integer length, Integer size) {
    Integer[] base = new Integer[length];
    for(Integer i=0;i<length;i++){
      base[i] = size;
    }
    mre = new MixedRadixEnumeration(base);
  }
  @Override
  public boolean hasMoreElements() {
    return mre.hasMoreElements();
  }

  @Override
  public Integer[] nextElement() {
    return mre.nextElement();
  }

}
