package name.NicolasCoutureGrenier.Music.PCS12Predicates;

import javax.annotation.Nonnull;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Music.PCS12;


public class ZeroTranspose implements StandardAndGuavaPredicate<PCS12> {

  public boolean apply(@Nonnull PCS12 input) {
    return input.getTranspose().equals(0);
  }

}
