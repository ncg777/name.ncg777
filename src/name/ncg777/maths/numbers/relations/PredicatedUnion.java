package name.ncg777.maths.numbers.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.Combination;
import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.Number;
import name.ncg777.maths.numbers.quartal.QuartalNumber;

public class PredicatedUnion implements BiPredicate<QuartalNumber, QuartalNumber> {
  private Predicate<Number> predicate;
  
  public PredicatedUnion(Cipher cipher, Predicate<Number> predicate){
    this.predicate = predicate;
  }
  
  @Override
  public boolean test(QuartalNumber a, QuartalNumber b) {
    return predicate.test(
        new Number(
            Cipher.Name.Binary,
            Combination.merge(a.toBinaryWord(), b.toBinaryWord())
            )
        );
  }
}