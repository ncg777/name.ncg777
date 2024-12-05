package name.ncg777.music.pitchClassSet12.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.music.pitchClassSet12.PitchClassSet12;

public class Bypass implements StandardAndGuavaPredicate<PitchClassSet12>  {

  @Override
  public boolean apply(@Nonnull PitchClassSet12 arg0) {
    return true;
  }

}
