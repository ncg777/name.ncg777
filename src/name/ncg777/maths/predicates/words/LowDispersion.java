package name.ncg777.maths.predicates.words;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.fuzzy.valuationFunctions.CombinationDispersion;
import name.ncg777.maths.objects.words.BinaryWord;

public class LowDispersion implements StandardAndGuavaPredicate<BinaryWord> {
  private CombinationDispersion cd = new CombinationDispersion();
  
  @Override
  public boolean apply(@Nonnull BinaryWord arg0) {
    return cd.apply(arg0).not().quantize(0.8);
  }

}
