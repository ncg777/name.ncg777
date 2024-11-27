package name.ncg777.musical.pitchClassSet12Predicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.mathematics.objects.Sequence;
import name.ncg777.musical.pitchClassSet12;

public class Consonant implements StandardAndGuavaPredicate<pitchClassSet12> {
  public boolean apply(@Nonnull pitchClassSet12 input) {
    Sequence IV = input.getIntervalVector();
    return IV.get(0) == 0 && IV.get(5)==0;
  }
}
