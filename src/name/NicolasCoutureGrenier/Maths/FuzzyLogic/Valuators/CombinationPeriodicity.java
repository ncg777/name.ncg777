package name.NicolasCoutureGrenier.Maths.FuzzyLogic.Valuators;

import com.google.common.base.Function;

import name.NicolasCoutureGrenier.Maths.DataStructures.Combination;
import name.NicolasCoutureGrenier.Maths.FuzzyLogic.FuzzyVariable;

public class CombinationPeriodicity implements Function<Combination, FuzzyVariable>  {
  private static CompositionPeriodicity cp = new CompositionPeriodicity();
  
  @Override
  public FuzzyVariable apply(Combination arg0) {
    return cp.apply(arg0.getComposition());
  }

}
