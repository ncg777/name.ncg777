package name.ncg777.maths.numbers.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.fuzzy.valuationFunctions.CombinationPeriodicity;
import name.ncg777.maths.numbers.BinaryNumber;

public class Periodic implements StandardAndGuavaPredicate<BinaryNumber>  {

  private static CombinationPeriodicity cp = new CombinationPeriodicity();
  
  @Override
  public boolean apply(@Nonnull BinaryNumber arg0) {
    return cp.apply(arg0).quantize(0.999);
  }
  

}
