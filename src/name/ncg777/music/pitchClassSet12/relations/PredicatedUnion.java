package name.ncg777.music.pitchClassSet12.relations;


import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.music.pitchClassSet12.PitchClassSet12;

public class PredicatedUnion implements BiPredicate<PitchClassSet12, PitchClassSet12> {
  Predicate<PitchClassSet12> f;

  public PredicatedUnion(Predicate<PitchClassSet12> p) {
    f = p;
  }
  @Override
  public boolean test(PitchClassSet12 a, PitchClassSet12 b) {
    return f.test(a.combineWith(b));
  }

}
