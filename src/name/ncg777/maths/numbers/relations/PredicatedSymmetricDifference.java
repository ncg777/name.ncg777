package name.ncg777.maths.numbers.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.numbers.BinaryNumber;

public class PredicatedSymmetricDifference implements BiPredicate<BinaryNumber, BinaryNumber> {
  private Predicate<BinaryNumber> predicate;
  
  public PredicatedSymmetricDifference(Predicate<BinaryNumber> predicate){
    this.predicate = predicate;
  }
  
  @Override
  public boolean test(BinaryNumber a, BinaryNumber b) {
    if(a.getN() != b.getN()) throw new IllegalArgumentException();
    return predicate.test(new BinaryNumber(a.symmetricDifference(b),a.getN()));
  }
}