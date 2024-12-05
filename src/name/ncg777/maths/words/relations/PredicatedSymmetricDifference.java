package name.ncg777.maths.words.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import name.ncg777.maths.words.BinaryWord;

public class PredicatedSymmetricDifference implements BiPredicate<BinaryWord, BinaryWord> {
  private Predicate<BinaryWord> predicate;
  
  public PredicatedSymmetricDifference(Predicate<BinaryWord> predicate){
    this.predicate = predicate;
  }
  
  @Override
  public boolean test(BinaryWord a, BinaryWord b) {
    if(a.getN() != b.getN()) throw new IllegalArgumentException();
    return predicate.test(new BinaryWord(a.symmetricDifference(b),a.getN()));
  }
}