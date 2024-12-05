package name.ncg777.music.pitchClassSet12.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.music.pitchClassSet12.PitchClassSet12;

public class SupersetOf implements StandardAndGuavaPredicate<PitchClassSet12>  {

  private PitchClassSet12 PitchClassSet12;
  public SupersetOf(PitchClassSet12 PitchClassSet12) {
    this.PitchClassSet12 = PitchClassSet12;
  }
  @Override
  public boolean apply(@Nonnull PitchClassSet12 input) {
    return PitchClassSet12.combineWith(input).getK() == input.getK();
  }

}
