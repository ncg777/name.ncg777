package name.ncg777.maths.numbers.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.numbers.BinaryNatural;

public class PredicatedSymmetricDifference implements BiPredicate<BinaryNatural, BinaryNatural> {
  private Predicate<BinaryNatural> predicate;
  
  public PredicatedSymmetricDifference(Predicate<BinaryNatural> predicate){
    this.predicate = predicate;
  }
  
  @Override
  public boolean test(BinaryNatural a, BinaryNatural b) {
    if(a.getN() != b.getN()) throw new IllegalArgumentException();
    return predicate.test(new BinaryNatural(a.symmetricDifference(b),a.getN()));
  }
}