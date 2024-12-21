package name.ncg777.maths.numbers.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.numbers.BinaryNumber;

public class PredicatedIntersection implements BiPredicate<BinaryNumber, BinaryNumber> {
  private Predicate<BinaryNumber> predicate;
  
  public PredicatedIntersection(Predicate<BinaryNumber> predicate){
    this.predicate = predicate;
  }
  
  @Override
  public boolean test(BinaryNumber a, BinaryNumber b) {
    if(a.getN() != b.getN()) throw new IllegalArgumentException();
    int n = a.getN();
    return predicate.test(new BinaryNumber(a.intersect(b),n));
  }
}