package name.ncg.Music.PCS12Relations;

import name.ncg.Maths.Relations.Relation;
import name.ncg.Music.PCS12;

import com.google.common.base.Predicate;

public class PredicatedDifferences implements Relation<PCS12, PCS12> {
  Predicate<PCS12> f;

  public PredicatedDifferences(Predicate<PCS12> p) {
    f = p;
  }

  @Override
  public boolean apply(PCS12 a, PCS12 b) {
    if (a == null || b == null) {
      return false;
    }

    return f.apply(b.minus(a)) && f.apply(a.minus(b));
  }

}
