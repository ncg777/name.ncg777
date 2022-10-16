package name.ncg.Maths.FuzzyLogic.Valuators;

import com.google.common.base.Function;

import name.ncg.Maths.DataStructures.Combination;
import name.ncg.Maths.FuzzyLogic.FuzzyVariable;

public class CombinationDispersion implements Function<Combination, FuzzyVariable> {
  CompositionDispersion v;

  public CombinationDispersion() {
    v = new CompositionDispersion();
  }

  @Override
  public FuzzyVariable apply(Combination input) {
    return v.apply(input.getComposition());
  }
}
