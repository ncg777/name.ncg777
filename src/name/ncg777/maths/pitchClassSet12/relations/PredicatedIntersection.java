package name.ncg777.maths.pitchClassSet12.relations;


import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.pitchClassSet12.PitchClassSet12;

public class PredicatedIntersection implements BiPredicate<PitchClassSet12, PitchClassSet12> {
  Predicate<PitchClassSet12> f;

  public PredicatedIntersection(Predicate<PitchClassSet12> p) {
    f = p;
  }
  @Override
  public boolean test(PitchClassSet12 a, PitchClassSet12 b) {

    return f.test(a.intersect(b));
  }

}
