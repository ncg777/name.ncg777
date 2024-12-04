package name.ncg777.maths.words.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.fuzzy.valuationFunctions.CombinationPeriodicity;
import name.ncg777.maths.objects.WordBinary;

public class Periodic implements StandardAndGuavaPredicate<WordBinary>  {

  private static CombinationPeriodicity cp = new CombinationPeriodicity();
  
  @Override
  public boolean apply(@Nonnull WordBinary arg0) {
    return cp.apply(arg0).quantize(0.999);
  }
  

}
