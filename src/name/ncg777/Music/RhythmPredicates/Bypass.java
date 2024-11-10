package name.ncg777.Music.RhythmPredicates;

import javax.annotation.Nonnull;

import name.ncg777.CS.Functional.StandardAndGuavaPredicate;
import name.ncg777.Music.Rhythm;

public class Bypass implements StandardAndGuavaPredicate<Rhythm> {

  @Override
  public boolean apply(@Nonnull Rhythm arg0) {
    // TODO Auto-generated method stub
    return true;
  }

}
