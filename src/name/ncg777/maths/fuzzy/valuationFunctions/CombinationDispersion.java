package name.ncg777.maths.fuzzy.valuationFunctions;

import com.google.common.base.Function;

import name.ncg777.maths.Combination;
import name.ncg777.maths.fuzzy.FuzzyVariable;

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
