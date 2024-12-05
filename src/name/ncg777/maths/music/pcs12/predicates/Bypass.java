package name.ncg777.maths.music.pcs12.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.music.pcs12.Pcs12;

public class Bypass implements StandardAndGuavaPredicate<Pcs12>  {

  @Override
  public boolean apply(@Nonnull Pcs12 arg0) {
    return true;
  }

}
