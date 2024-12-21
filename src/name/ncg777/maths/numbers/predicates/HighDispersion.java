package name.ncg777.maths.numbers.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.fuzzy.valuationFunctions.CombinationDispersion;
import name.ncg777.maths.numbers.BinaryNumber;

public class HighDispersion implements StandardAndGuavaPredicate<BinaryNumber> {
  private CombinationDispersion cd = new CombinationDispersion();
  
  @Override
  public boolean apply(@Nonnull BinaryNumber arg0) {
    return cd.apply(arg0).quantize(0.65);
  }

}
