package name.ncg777.maths.words.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import name.ncg777.maths.words.BinaryWord;

public class PredicatedIntersection implements BiPredicate<BinaryWord, BinaryWord> {
  private Predicate<BinaryWord> predicate;
  
  public PredicatedIntersection(Predicate<BinaryWord> predicate){
    this.predicate = predicate;
  }
  
  @Override
  public boolean test(BinaryWord a, BinaryWord b) {
    if(a.getN() != b.getN()) throw new IllegalArgumentException();
    int n = a.getN();
    return predicate.test(new BinaryWord(a.intersect(b),n));
  }
}