package name.ncg777.Maths.Enumerations;

import java.util.ArrayList;
import java.util.Enumeration;

public class KPermutationEnumeration implements Enumeration<ArrayList<Integer>>{

  private MixedRadixEnumeration mre;
  private Integer n;
  private Integer k;
  public KPermutationEnumeration(Integer n, Integer k){
    this.n = n;
    this.k = k;
    Integer[] base = new Integer[k];
    for(Integer i=0;i<k;i++){
      base[i] = n - i;
    }
    mre = new MixedRadixEnumeration(base);
    
  }

  @Override
  public boolean hasMoreElements() {
    return mre.hasMoreElements();
  }

  @Override
  public ArrayList<Integer> nextElement() {
    Integer[] m = mre.nextElement();
    ArrayList<Integer> o = new ArrayList<Integer>();
    ArrayList<Integer> l = new ArrayList<Integer>();
    for(Integer i=0;i<n;i++){l.add(i);}
    for(Integer i=0;i<k;i++){o.add(l.get(m[i])); l.remove(m[i]);}
    return o;
  }
}
