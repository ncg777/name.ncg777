package name.ncg777.maths.numbers.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.numbers.BinaryNatural;

public class PredicatedIntersection implements BiPredicate<BinaryNatural, BinaryNatural> {
  private Predicate<BinaryNatural> predicate;
  
  public PredicatedIntersection(Predicate<BinaryNatural> predicate){
    this.predicate = predicate;
  }
  
  @Override
  public boolean test(BinaryNatural a, BinaryNatural b) {
    if(a.getN() != b.getN()) throw new IllegalArgumentException();
    int n = a.getN();
    return predicate.test(new BinaryNatural(a.intersect(b),n));
  }
}