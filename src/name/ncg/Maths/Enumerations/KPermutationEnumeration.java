package name.ncg.Maths.Enumerations;

import java.util.ArrayList;
import java.util.Enumeration;

public class KPermutationEnumeration implements Enumeration<ArrayList<Integer>>{

  private MixedRadixEnumeration mre;
  private int n;
  private int k;
  public KPermutationEnumeration(int n, int k){
    this.n = n;
    this.k = k;
    int[] base = new int[k];
    for(int i=0;i<k;i++){
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
    int[] m = mre.nextElement();
    ArrayList<Integer> o = new ArrayList<Integer>();
    ArrayList<Integer> l = new ArrayList<Integer>();
    for(int i=0;i<n;i++){l.add(i);}
    for(int i=0;i<k;i++){o.add(l.get(m[i])); l.remove(m[i]);}
    return o;
  }
}
