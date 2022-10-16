package name.ncg.Music.RhythmPredicates;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Maths.FuzzyLogic.Valuators.CombinationPeriodicity;
import name.ncg.Music.Rhythm;

public class Periodic implements StandardAndGuavaPredicate<Rhythm>  {

  private static CombinationPeriodicity cp = new CombinationPeriodicity();
  
  @Override
  public boolean apply(Rhythm arg0) {
    return cp.apply(arg0).quantize(0.999);
  }
  

}
