package name.ncg.Music.PCS12Predicates;

import com.google.common.base.Predicate;

import name.ncg.Music.PCS12;

public class Bypass implements Predicate<PCS12>  {

  @Override
  public boolean apply(PCS12 arg0) {
    return true;
  }

}
