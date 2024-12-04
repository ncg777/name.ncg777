package name.ncg777.music.pitchClassSet12.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.objects.Sequence;
import name.ncg777.music.pitchClassSet12.PitchClassSet12;

public class FifthMax implements StandardAndGuavaPredicate<PitchClassSet12>   {

  @Override
  public boolean apply(@Nonnull PitchClassSet12 arg0) {
    Sequence s = arg0.getIntervalVector();
    
    return s.get(4).equals(s.getMax());
  }

}
