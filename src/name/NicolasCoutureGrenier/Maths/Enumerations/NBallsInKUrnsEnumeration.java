package name.NicolasCoutureGrenier.Maths.Enumerations;

import java.util.Enumeration;

import name.NicolasCoutureGrenier.Maths.DataStructures.Combination;

public class NBallsInKUrnsEnumeration implements Enumeration<Integer[]>  {

  private CombinationEnumeration ce = null;
  
  public NBallsInKUrnsEnumeration(int n, int k) {
    this.ce = new CombinationEnumeration(n+k-1, k-1);
  }
  
  @Override
  public boolean hasMoreElements() {
    return ce.hasMoreElements();
  }

  @Override
  public Integer[] nextElement() {
    return convertCombination(ce.nextElement());
  }
  
  private Integer[] convertCombination(Combination c) {
    Integer[] o = new Integer[c.getK()+1];
    int i = 0;
    int cnt = 0;
    for(int j=0;j<c.getN();j++) {
      if(c.get(j)) { // is a bar
        o[i++] = cnt;
        cnt=0;
      } else { // is a star
        cnt++;
      }
    }
    o[i]=cnt;
    
    return o;
  }
}
