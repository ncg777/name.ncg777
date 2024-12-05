package name.ncg777.maths.music.pcs12.relations;


import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.music.pcs12.Pcs12;


public class PredicatedSymmetricDifference implements BiPredicate<Pcs12, Pcs12> {
  Predicate<Pcs12> f;

  public PredicatedSymmetricDifference(Predicate<Pcs12> p) {
    f = p;
  }

  @Override
  public boolean test(Pcs12 a, Pcs12 b) {
    if (a == null || b == null) {
      return false;
    }
    return f.test(a.symmetricDifference(b));
  }

}
