package name.ncg777.Maths.FuzzyLogic.Valuators;

import com.google.common.base.Function;

import name.ncg777.Maths.FuzzyLogic.FuzzyVariable;
import name.ncg777.Maths.Objects.Combination;

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
