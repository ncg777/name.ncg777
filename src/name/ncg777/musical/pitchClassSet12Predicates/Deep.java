package name.ncg777.musical.pitchClassSet12Predicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.mathematics.objects.Sequence;
import name.ncg777.musical.pitchClassSet12;

public class Deep implements StandardAndGuavaPredicate<pitchClassSet12>  {

  @Override
  public boolean apply(@Nonnull pitchClassSet12 arg0) {
    Sequence s = arg0.getIntervalVector();
    
    return s.getMin() == s.getMax();
  }

}
