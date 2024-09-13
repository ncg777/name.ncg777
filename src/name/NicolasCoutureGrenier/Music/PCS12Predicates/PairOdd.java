package name.NicolasCoutureGrenier.Music.PCS12Predicates;

import javax.annotation.Nonnull;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Maths.Objects.Sequence;
import name.NicolasCoutureGrenier.Music.PCS12;

public class PairOdd implements StandardAndGuavaPredicate<PCS12>   {

  @Override
  public boolean apply(@Nonnull PCS12 arg0) {
    Sequence s = arg0.getIntervalVector();
    int k = s.get(0) % 2;
    for(int i=0;i<s.size();i++){
      if(s.get(i)%2 != k){return false;}
      k = (k==0)?1:0;
    }
    return true;
  }

}
