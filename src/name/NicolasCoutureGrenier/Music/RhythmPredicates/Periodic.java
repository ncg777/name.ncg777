package name.NicolasCoutureGrenier.Music.RhythmPredicates;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Maths.FuzzyLogic.Valuators.CombinationPeriodicity;
import name.NicolasCoutureGrenier.Music.Rhythm;

public class Periodic implements StandardAndGuavaPredicate<Rhythm>  {

  private static CombinationPeriodicity cp = new CombinationPeriodicity();
  
  @Override
  public boolean apply(Rhythm arg0) {
    return cp.apply(arg0).quantize(0.999);
  }
  

}
