package name.NicolasCoutureGrenier.Music.PCS12Predicates;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;
import name.NicolasCoutureGrenier.Music.PCS12;

public class Deep implements StandardAndGuavaPredicate<PCS12>  {

  @Override
  public boolean apply(PCS12 arg0) {
    Sequence s = arg0.getIntervalVector();
    
    return s.getMin() == s.getMax();
  }

}
