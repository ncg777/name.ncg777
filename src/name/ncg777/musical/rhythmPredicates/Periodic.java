package name.ncg777.musical.rhythmPredicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.mathematics.fuzzy.valuationFunctions.CombinationPeriodicity;
import name.ncg777.musical.Rhythm;

public class Periodic implements StandardAndGuavaPredicate<Rhythm>  {

  private static CombinationPeriodicity cp = new CombinationPeriodicity();
  
  @Override
  public boolean apply(@Nonnull Rhythm arg0) {
    return cp.apply(arg0).quantize(0.999);
  }
  

}
