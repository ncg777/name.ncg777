package name.ncg777.musical.pitchClassSet12Predicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.musical.pitchClassSet12;

public class ZeroTranspose implements StandardAndGuavaPredicate<pitchClassSet12> {

  public boolean apply(@Nonnull pitchClassSet12 input) {
    return input.getTranspose().equals(0);
  }

}
