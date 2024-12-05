package name.ncg777.maths.words.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.fuzzy.valuationFunctions.CombinationPeriodicity;
import name.ncg777.maths.words.BinaryWord;

public class Periodic implements StandardAndGuavaPredicate<BinaryWord>  {

  private static CombinationPeriodicity cp = new CombinationPeriodicity();
  
  @Override
  public boolean apply(@Nonnull BinaryWord arg0) {
    return cp.apply(arg0).quantize(0.999);
  }
  

}
