package name.ncg777.maths.numbers.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.numbers.BinaryNumber;

public class PredicatedDifferences implements BiPredicate<BinaryNumber, BinaryNumber> {
  private Predicate<BinaryNumber> predicate;
  
  public PredicatedDifferences(Predicate<BinaryNumber> predicate){
    this.predicate = predicate;
  }
  
  @Override
  public boolean test(BinaryNumber a, BinaryNumber b) {
    if(a.getN() != b.getN()) throw new IllegalArgumentException();
    int n = a.getN();
    var d1 = new BinaryNumber(a.minus(b), n);
    var d2 = new BinaryNumber(b.minus(a), n);
    
    return predicate.test(d1) && predicate.test(d2); 
  }
}
