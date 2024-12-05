package name.ncg777.maths.music.pcs12.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.music.pcs12.Pcs12;
import name.ncg777.maths.sequences.Sequence;

public class PairOdd implements StandardAndGuavaPredicate<Pcs12>   {

  @Override
  public boolean apply(@Nonnull Pcs12 arg0) {
    Sequence s = arg0.getIntervalVector();
    int k = s.get(0) % 2;
    for(int i=0;i<s.size();i++){
      if(s.get(i)%2 != k){return false;}
      k = (k==0)?1:0;
    }
    return true;
  }

}
