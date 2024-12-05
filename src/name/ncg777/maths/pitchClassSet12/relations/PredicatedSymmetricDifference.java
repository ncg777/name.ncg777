package name.ncg777.maths.pitchClassSet12.relations;


import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.pitchClassSet12.PitchClassSet12;


public class PredicatedSymmetricDifference implements BiPredicate<PitchClassSet12, PitchClassSet12> {
  Predicate<PitchClassSet12> f;

  public PredicatedSymmetricDifference(Predicate<PitchClassSet12> p) {
    f = p;
  }

  @Override
  public boolean test(PitchClassSet12 a, PitchClassSet12 b) {
    if (a == null || b == null) {
      return false;
    }
    return f.test(a.symmetricDifference(b));
  }

}
