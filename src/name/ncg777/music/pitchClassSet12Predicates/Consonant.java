package name.ncg777.music.pitchClassSet12Predicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.objects.Sequence;
import name.ncg777.music.PitchClassSet12;

public class Consonant implements StandardAndGuavaPredicate<PitchClassSet12> {
  public boolean apply(@Nonnull PitchClassSet12 input) {
    Sequence IV = input.getIntervalVector();
    return IV.get(0) == 0 && IV.get(5)==0;
  }
}
