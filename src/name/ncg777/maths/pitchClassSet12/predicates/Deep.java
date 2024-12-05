package name.ncg777.maths.pitchClassSet12.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.pitchClassSet12.PitchClassSet12;
import name.ncg777.maths.sequences.Sequence;

public class Deep implements StandardAndGuavaPredicate<PitchClassSet12>  {

  @Override
  public boolean apply(@Nonnull PitchClassSet12 arg0) {
    Sequence s = arg0.getIntervalVector();
    
    return s.getMin() == s.getMax();
  }

}
