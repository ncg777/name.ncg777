package name.ncg777.maths.words.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import name.ncg777.maths.words.Alphabet;
import name.ncg777.maths.words.Tetragraph;
import name.ncg777.maths.words.Word;

public class PredicatedIntersectionAndSymmetricDifference implements BiPredicate<Tetragraph, Tetragraph> {

  private PredicatedIntersection p1;
  private PredicatedSymmetricDifference p2;
  
  public PredicatedIntersectionAndSymmetricDifference(
      Alphabet alphabet, Predicate<Word> predicate) {
    p1 = new PredicatedIntersection(alphabet, predicate);
    p2 = new PredicatedSymmetricDifference(alphabet, predicate);
  }
  @Override
  public boolean test(Tetragraph t, Tetragraph u) {
    return p1.test(t, u) && p2.test(t, u);
  }
  
}