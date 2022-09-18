package name.ncg.Music.PCS12Predicates;

import com.google.common.base.Predicate;
import name.ncg.Music.PCS12;


public class ZeroTranspose implements Predicate<PCS12> {

  public boolean apply(PCS12 input) {
    return input.getTranspose().equals(0);
  }

}
