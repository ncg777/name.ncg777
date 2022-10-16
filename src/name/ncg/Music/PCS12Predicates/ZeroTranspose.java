package name.ncg.Music.PCS12Predicates;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Music.PCS12;


public class ZeroTranspose implements StandardAndGuavaPredicate<PCS12> {

  public boolean apply(PCS12 input) {
    return input.getTranspose().equals(0);
  }

}
