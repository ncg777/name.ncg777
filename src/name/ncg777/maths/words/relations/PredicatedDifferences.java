package name.ncg777.maths.words.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.words.BinaryWord;

public class PredicatedDifferences implements BiPredicate<BinaryWord, BinaryWord> {
  private Predicate<BinaryWord> predicate;
  
  public PredicatedDifferences(Predicate<BinaryWord> predicate){
    this.predicate = predicate;
  }
  
  @Override
  public boolean test(BinaryWord a, BinaryWord b) {
    if(a.getN() != b.getN()) throw new IllegalArgumentException();
    int n = a.getN();
    var d1 = new BinaryWord(a.minus(b), n);
    var d2 = new BinaryWord(b.minus(a), n);
    
    return predicate.test(d1) && predicate.test(d2); 
  }
}
