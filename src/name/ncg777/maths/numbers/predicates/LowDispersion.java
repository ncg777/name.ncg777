package name.ncg777.maths.numbers.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.fuzzy.valuationFunctions.CombinationDispersion;
import name.ncg777.maths.numbers.BinaryNatural;

public class LowDispersion implements StandardAndGuavaPredicate<BinaryNatural> {
  private CombinationDispersion cd = new CombinationDispersion();
  
  @Override
  public boolean apply(@Nonnull BinaryNatural arg0) {
    return cd.apply(arg0).not().quantize(0.8);
  }

}
