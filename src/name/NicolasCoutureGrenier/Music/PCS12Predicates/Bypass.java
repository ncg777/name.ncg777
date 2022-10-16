package name.NicolasCoutureGrenier.Music.PCS12Predicates;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Music.PCS12;

public class Bypass implements StandardAndGuavaPredicate<PCS12>  {

  @Override
  public boolean apply(PCS12 arg0) {
    return true;
  }

}
