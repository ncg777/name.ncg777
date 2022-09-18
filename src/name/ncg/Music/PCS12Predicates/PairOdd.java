package name.ncg.Music.PCS12Predicates;

import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.PCS12;

import com.google.common.base.Predicate;

public class PairOdd implements Predicate<PCS12>   {

  @Override
  public boolean apply(PCS12 arg0) {
    Sequence s = arg0.getIntervalVector();
    int k = s.get(0) % 2;
    for(int i=0;i<s.size();i++){
      if(s.get(i)%2 != k){return false;}
      k = (k==0)?1:0;
    }
    return true;
  }

}
