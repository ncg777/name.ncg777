package name.ncg777.maths.words.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.fuzzy.valuationFunctions.CombinationDispersion;
import name.ncg777.maths.words.BinaryWord;

public class HighDispersion implements StandardAndGuavaPredicate<BinaryWord> {
  private CombinationDispersion cd = new CombinationDispersion();
  
  @Override
  public boolean apply(@Nonnull BinaryWord arg0) {
    return cd.apply(arg0).quantize(0.65);
  }

}
