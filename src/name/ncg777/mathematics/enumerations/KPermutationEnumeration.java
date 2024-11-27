package name.ncg777.mathematics.enumerations;

import java.util.Enumeration;

import name.ncg777.mathematics.objects.Sequence;

public class KPermutationEnumeration implements Enumeration<Sequence>{

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
  public Sequence nextElement() {
    int[] m = mre.nextElement();
    var o = new Sequence();
    var l = new Sequence();
    for(int i=0;i<n;i++){l.add(i);}
    for(int i=0;i<k;i++){o.add(l.get(m[i])); l.remove(m[i]);}
    return o;
  }
}
