package name.ncg777.maths.numbers.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.numbers.BinaryNatural;

public class PredicatedIntersectionAndSymmetricDifference implements BiPredicate<BinaryNatural, BinaryNatural> {

  private PredicatedIntersection p1;
  private PredicatedSymmetricDifference p2;
  
  public PredicatedIntersectionAndSymmetricDifference(Predicate<BinaryNatural> predicate) {
    p1 = new PredicatedIntersection(predicate);
    p2 = new PredicatedSymmetricDifference(predicate);
  }
  @Override
  public boolean test(BinaryNatural t, BinaryNatural u) {
    return p1.test(t, u) && p2.test(t, u);
  }
  
}