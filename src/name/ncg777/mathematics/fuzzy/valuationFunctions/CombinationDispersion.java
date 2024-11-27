package name.ncg777.mathematics.fuzzy.valuationFunctions;

import com.google.common.base.Function;

import name.ncg777.mathematics.fuzzy.FuzzyVariable;
import name.ncg777.mathematics.objects.Combination;

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
