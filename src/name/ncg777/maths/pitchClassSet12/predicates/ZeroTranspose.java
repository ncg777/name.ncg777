package name.ncg777.maths.pitchClassSet12.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.pitchClassSet12.PitchClassSet12;

public class ZeroTranspose implements StandardAndGuavaPredicate<PitchClassSet12> {

  public boolean apply(@Nonnull PitchClassSet12 input) {
    return input.getTranspose().equals(0);
  }

}
