package name.NicolasCoutureGrenier.Maths.FuzzyLogic.Valuators;

import com.google.common.base.Function;

import name.NicolasCoutureGrenier.Maths.DataStructures.Composition;
import name.NicolasCoutureGrenier.Maths.FuzzyLogic.FuzzyVariable;

public class CompositionPeriodicity implements Function<Composition, FuzzyVariable> {

  private static SequencePeriodicity sp = new SequencePeriodicity();
  @Override
  public FuzzyVariable apply(Composition arg0) {
    return sp.apply(arg0.asSequence());
  }  

}
