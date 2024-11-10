package name.ncg777.Music.PCS12Relations;


import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.Music.PCS12;

public class PredicatedIntersection implements BiPredicate<PCS12, PCS12> {
  Predicate<PCS12> f;

  public PredicatedIntersection(Predicate<PCS12> p) {
    f = p;
  }
  @Override
  public boolean test(PCS12 a, PCS12 b) {

    return f.test(a.intersect(b));
  }

}
