package name.ncg777.maths.numbers.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.Combination;
import name.ncg777.maths.numbers.Alphabet;
import name.ncg777.maths.numbers.QuartalNumber;
import name.ncg777.maths.numbers.Number;

public class PredicatedUnion implements BiPredicate<QuartalNumber, QuartalNumber> {
  private Predicate<Number> predicate;
  
  public PredicatedUnion(Alphabet alphabet, Predicate<Number> predicate){
    this.predicate = predicate;
  }
  
  @Override
  public boolean test(QuartalNumber a, QuartalNumber b) {
    return predicate.test(
        new Number(
            Alphabet.Name.Binary,
            Combination.merge(a.toBinaryWord(), b.toBinaryWord())
            )
        );
  }
}