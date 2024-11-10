package name.ncg777.Music.RhythmPredicates;

import javax.annotation.Nonnull;

import name.ncg777.CS.Functional.StandardAndGuavaPredicate;
import name.ncg777.Maths.FuzzyLogic.Valuators.CombinationPeriodicity;
import name.ncg777.Music.Rhythm;

public class Periodic implements StandardAndGuavaPredicate<Rhythm>  {

  private static CombinationPeriodicity cp = new CombinationPeriodicity();
  
  @Override
  public boolean apply(@Nonnull Rhythm arg0) {
    return cp.apply(arg0).quantize(0.999);
  }
  

}
