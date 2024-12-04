package name.ncg777.music.pitchClassSet12Relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.music.PitchClassSet12;

public class PredicatedDifferences implements BiPredicate<PitchClassSet12, PitchClassSet12> {
  Predicate<PitchClassSet12> f;

  public PredicatedDifferences(Predicate<PitchClassSet12> p) {
    f = p;
  }

  @Override
  public boolean test(PitchClassSet12 a, PitchClassSet12 b) {
    if (a == null || b == null) {
      return false;
    }

    return f.test(b.minus(a)) && f.test(a.minus(b));
  }

}
