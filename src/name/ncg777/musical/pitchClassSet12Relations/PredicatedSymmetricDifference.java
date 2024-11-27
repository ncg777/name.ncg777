package name.ncg777.musical.pitchClassSet12Relations;


import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.musical.pitchClassSet12;


public class PredicatedSymmetricDifference implements BiPredicate<pitchClassSet12, pitchClassSet12> {
  Predicate<pitchClassSet12> f;

  public PredicatedSymmetricDifference(Predicate<pitchClassSet12> p) {
    f = p;
  }

  @Override
  public boolean test(pitchClassSet12 a, pitchClassSet12 b) {
    if (a == null || b == null) {
      return false;
    }
    return f.test(a.symmetricDifference(b));
  }

}
