package name.ncg.Music.PCS12Predicates;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Music.PCS12;

public class Bypass implements StandardAndGuavaPredicate<PCS12>  {

  @Override
  public boolean apply(PCS12 arg0) {
    return true;
  }

}
