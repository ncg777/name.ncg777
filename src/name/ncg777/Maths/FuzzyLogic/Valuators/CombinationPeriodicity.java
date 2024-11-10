package name.ncg777.Maths.FuzzyLogic.Valuators;

import com.google.common.base.Function;

import name.ncg777.Maths.FuzzyLogic.FuzzyVariable;
import name.ncg777.Maths.Objects.Combination;

public class CombinationPeriodicity implements Function<Combination, FuzzyVariable>  {
  private static CompositionPeriodicity cp = new CompositionPeriodicity();
  
  @Override
  public FuzzyVariable apply(Combination arg0) {
    return cp.apply(arg0.getComposition());
  }

}
