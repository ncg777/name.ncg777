package name.ncg777.maths.fuzzy.valuationFunctions;

import com.google.common.base.Function;

import name.ncg777.maths.Combination;
import name.ncg777.maths.fuzzy.FuzzyVariable;

public class CombinationPeriodicity implements Function<Combination, FuzzyVariable>  {
  private static CompositionPeriodicity cp = new CompositionPeriodicity();
  
  @Override
  public FuzzyVariable apply(Combination arg0) {
    return cp.apply(arg0.getComposition());
  }

}
