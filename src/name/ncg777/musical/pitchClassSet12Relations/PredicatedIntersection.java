package name.ncg777.musical.pitchClassSet12Relations;


import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.musical.pitchClassSet12;

public class PredicatedIntersection implements BiPredicate<pitchClassSet12, pitchClassSet12> {
  Predicate<pitchClassSet12> f;

  public PredicatedIntersection(Predicate<pitchClassSet12> p) {
    f = p;
  }
  @Override
  public boolean test(pitchClassSet12 a, pitchClassSet12 b) {

    return f.test(a.intersect(b));
  }

}
