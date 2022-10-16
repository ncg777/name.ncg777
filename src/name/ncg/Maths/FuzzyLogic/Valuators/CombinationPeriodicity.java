package name.ncg.Maths.FuzzyLogic.Valuators;

import com.google.common.base.Function;

import name.ncg.Maths.DataStructures.Combination;
import name.ncg.Maths.FuzzyLogic.FuzzyVariable;

public class CombinationPeriodicity implements Function<Combination, FuzzyVariable>  {
  private static CompositionPeriodicity cp = new CompositionPeriodicity();
  
  @Override
  public FuzzyVariable apply(Combination arg0) {
    return cp.apply(arg0.getComposition());
  }

}
