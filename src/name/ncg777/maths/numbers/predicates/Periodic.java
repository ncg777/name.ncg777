package name.ncg777.maths.numbers.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.fuzzy.valuationFunctions.CombinationPeriodicity;
import name.ncg777.maths.numbers.BinaryNatural;

public class Periodic implements StandardAndGuavaPredicate<BinaryNatural>  {

  private static CombinationPeriodicity cp = new CombinationPeriodicity();
  
  @Override
  public boolean apply(@Nonnull BinaryNatural arg0) {
    return cp.apply(arg0).quantize(0.999);
  }
  

}
