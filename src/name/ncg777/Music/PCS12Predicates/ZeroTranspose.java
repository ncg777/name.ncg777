package name.ncg777.Music.PCS12Predicates;

import javax.annotation.Nonnull;

import name.ncg777.CS.Functional.StandardAndGuavaPredicate;
import name.ncg777.Music.PCS12;


public class ZeroTranspose implements StandardAndGuavaPredicate<PCS12> {

  public boolean apply(@Nonnull PCS12 input) {
    return input.getTranspose().equals(0);
  }

}
