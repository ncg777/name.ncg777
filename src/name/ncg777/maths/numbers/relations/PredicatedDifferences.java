package name.ncg777.maths.numbers.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.numbers.BinaryNatural;

public class PredicatedDifferences implements BiPredicate<BinaryNatural, BinaryNatural> {
  private Predicate<BinaryNatural> predicate;
  
  public PredicatedDifferences(Predicate<BinaryNatural> predicate){
    this.predicate = predicate;
  }
  
  @Override
  public boolean test(BinaryNatural a, BinaryNatural b) {
    if(a.getN() != b.getN()) throw new IllegalArgumentException();
    int n = a.getN();
    var d1 = new BinaryNatural(a.minus(b), n);
    var d2 = new BinaryNatural(b.minus(a), n);
    
    return predicate.test(d1) && predicate.test(d2); 
  }
}
