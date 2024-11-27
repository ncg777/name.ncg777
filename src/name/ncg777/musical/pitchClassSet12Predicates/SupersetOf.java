package name.ncg777.musical.pitchClassSet12Predicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.musical.pitchClassSet12;

public class SupersetOf implements StandardAndGuavaPredicate<pitchClassSet12>  {

  private pitchClassSet12 pitchClassSet12;
  public SupersetOf(pitchClassSet12 pitchClassSet12) {
    this.pitchClassSet12 = pitchClassSet12;
  }
  @Override
  public boolean apply(@Nonnull pitchClassSet12 input) {
    return pitchClassSet12.combineWith(input).getK() == input.getK();
  }

}
