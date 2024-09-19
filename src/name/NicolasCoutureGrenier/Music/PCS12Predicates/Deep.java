package name.NicolasCoutureGrenier.Music.PCS12Predicates;

import javax.annotation.Nonnull;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Maths.Objects.Sequence;
import name.NicolasCoutureGrenier.Music.PCS12;

public class Deep implements StandardAndGuavaPredicate<PCS12>  {

  @Override
  public boolean apply(@Nonnull PCS12 arg0) {
    Sequence s = arg0.getIntervalVector();
    
    return s.getMin() == s.getMax();
  }

}
