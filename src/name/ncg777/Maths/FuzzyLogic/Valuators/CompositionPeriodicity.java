package name.ncg777.Maths.FuzzyLogic.Valuators;

import com.google.common.base.Function;

import name.ncg777.Maths.FuzzyLogic.FuzzyVariable;
import name.ncg777.Maths.Objects.Composition;

public class CompositionPeriodicity implements Function<Composition, FuzzyVariable> {

  private static SequencePeriodicity sp = new SequencePeriodicity();
  @Override
  public FuzzyVariable apply(Composition arg0) {
    return sp.apply(arg0.asSequence());
  }  

}
