package name.ncg777.maths.music.pcs12.relations;


import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.music.pcs12.Pcs12;

public class PredicatedIntersection implements BiPredicate<Pcs12, Pcs12> {
  Predicate<Pcs12> f;

  public PredicatedIntersection(Predicate<Pcs12> p) {
    f = p;
  }
  @Override
  public boolean test(Pcs12 a, Pcs12 b) {

    return f.test(a.intersect(b));
  }

}
