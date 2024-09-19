package name.NicolasCoutureGrenier.Maths.Enumerations;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Set;

import org.apache.commons.collections4.bag.TreeBag;

import name.NicolasCoutureGrenier.Maths.Objects.Combination;

/**
 * This class enumerates multisets with a fixed size.
 * 
 * This is equivalent to the stars and bars counting problem allowing empty bins.
 * 
 * http://en.wikipedia.org/wiki/Stars_and_bars_%28combinatorics%29
 *  
 * @author Nicolas Couture-Grenier
 *
 * @param <T>
 * 
 * @see CombinationEnumeration
 */
public class  MultisetEnumeration<T> implements Enumeration<TreeBag<T>> {

  private CombinationEnumeration ce;
  private ArrayList<T> c = new ArrayList<T>();
  private int n;
  private int k;
  
  public MultisetEnumeration(Set<T> c, int totalCount){
    this.n = c.size();
    this.k = totalCount;
    this.c.addAll(c);
    ce = new CombinationEnumeration(n + k - 1, k);
   
  }
  
  private TreeBag<T> mapCombinationToBag(Combination comb){
    
    TreeBag<T> o = new TreeBag<T>();
    int currentBin = 0;
    
    for(int i=0;i<n+k-1;i++){
      if(comb.get(i)){ //star
        
        o.add(c.get(currentBin));
      } else{ // bar
        currentBin++;
      }
    }
    return o;
  }
  
  @Override
  public boolean hasMoreElements() {
    return ce.hasMoreElements();
  }

  @Override
  public TreeBag<T> nextElement() {
    return mapCombinationToBag(ce.nextElement());
  }

}
