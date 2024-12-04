package name.ncg777.maths.words.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.fuzzy.valuationFunctions.CombinationDispersion;
import name.ncg777.maths.objects.WordBinary;

public class HighDispersion implements StandardAndGuavaPredicate<WordBinary> {
  private CombinationDispersion cd = new CombinationDispersion();
  
  @Override
  public boolean apply(@Nonnull WordBinary arg0) {
    return cd.apply(arg0).quantize(0.65);
  }

}
