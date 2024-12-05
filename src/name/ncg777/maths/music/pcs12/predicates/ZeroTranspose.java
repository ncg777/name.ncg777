package name.ncg777.maths.music.pcs12.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.music.pcs12.Pcs12;

public class ZeroTranspose implements StandardAndGuavaPredicate<Pcs12> {

  public boolean apply(@Nonnull Pcs12 input) {
    return input.getTranspose().equals(0);
  }

}
