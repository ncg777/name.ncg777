package name.ncg.Music.PCS12Relations;

import com.google.common.base.Predicate;

import name.ncg.Maths.Relations.Relation;
import name.ncg.Music.PCS12;

public class PredicatedUnion implements Relation<PCS12, PCS12> {
  Predicate<PCS12> f;

  public PredicatedUnion(Predicate<PCS12> p) {
    f = p;
  }
  @Override
  public boolean apply(PCS12 a, PCS12 b) {
    return f.apply(a.combineWith(b));
  }

}
