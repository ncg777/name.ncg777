package name.NicolasCoutureGrenier.Maths.Enumerations;

import java.util.Enumeration;

import name.NicolasCoutureGrenier.Maths.DataStructures.Combination;

public class NBallsInKUrnsEnumeration implements Enumeration<Integer[]>  {

  private CombinationEnumeration ce = null;
  
  public NBallsInKUrnsEnumeration(int n, int k) {
    ce = new CombinationEnumeration(n+k-1, k-1);
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
    var s = c.asSequence();
    s.add(0, -1);
    s.add(c.getN()+c.getK()-2);
    
    Integer[] o = new Integer[c.getK()+1];
    
    for(int i=1;i<s.size();i++) {
      o[i-1] = s.get(i)-s.get(i-1)-1;
    }
    return o;
  }
}
