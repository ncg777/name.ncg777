package name.ncg777.Music.PCS12Predicates;

import javax.annotation.Nonnull;

import name.ncg777.CS.Functional.StandardAndGuavaPredicate;
import name.ncg777.Music.PCS12;

public class Bypass implements StandardAndGuavaPredicate<PCS12>  {

  @Override
  public boolean apply(@Nonnull PCS12 arg0) {
    return true;
  }

}
