package name.ncg777.mathematics.fuzzy.valuationFunctions;

import com.google.common.base.Function;

import name.ncg777.mathematics.fuzzy.FuzzyVariable;
import name.ncg777.mathematics.objects.Combination;

public class CombinationPeriodicity implements Function<Combination, FuzzyVariable>  {
  private static CompositionPeriodicity cp = new CompositionPeriodicity();
  
  @Override
  public FuzzyVariable apply(Combination arg0) {
    return cp.apply(arg0.getComposition());
  }

}
