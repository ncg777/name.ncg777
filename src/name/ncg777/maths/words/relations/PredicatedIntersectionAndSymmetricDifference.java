package name.ncg777.maths.words.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import name.ncg777.maths.words.BinaryWord;

public class PredicatedIntersectionAndSymmetricDifference implements BiPredicate<BinaryWord, BinaryWord> {

  private PredicatedIntersection p1;
  private PredicatedSymmetricDifference p2;
  
  public PredicatedIntersectionAndSymmetricDifference(Predicate<BinaryWord> predicate) {
    p1 = new PredicatedIntersection(predicate);
    p2 = new PredicatedSymmetricDifference(predicate);
  }
  @Override
  public boolean test(BinaryWord t, BinaryWord u) {
    return p1.test(t, u) && p2.test(t, u);
  }
  
}