package name.ncg777.maths.numbers.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.numbers.BinaryNumber;

public class PredicatedIntersectionAndSymmetricDifference implements BiPredicate<BinaryNumber, BinaryNumber> {

  private PredicatedIntersection p1;
  private PredicatedSymmetricDifference p2;
  
  public PredicatedIntersectionAndSymmetricDifference(Predicate<BinaryNumber> predicate) {
    p1 = new PredicatedIntersection(predicate);
    p2 = new PredicatedSymmetricDifference(predicate);
  }
  @Override
  public boolean test(BinaryNumber t, BinaryNumber u) {
    return p1.test(t, u) && p2.test(t, u);
  }
  
}