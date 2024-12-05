package name.ncg777.maths.fuzzy.valuationFunctions;

import com.google.common.base.Function;

import name.ncg777.maths.Composition;
import name.ncg777.maths.fuzzy.FuzzyVariable;

public class CompositionPeriodicity implements Function<Composition, FuzzyVariable> {

  private static SequencePeriodicity sp = new SequencePeriodicity();
  @Override
  public FuzzyVariable apply(Composition arg0) {
    return sp.apply(arg0.asSequence());
  }  

}
