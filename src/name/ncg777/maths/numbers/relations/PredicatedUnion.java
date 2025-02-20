package name.ncg777.maths.numbers.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.Combination;
import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.Natural;
import name.ncg777.maths.numbers.fixed.FixedLength;

public class PredicatedUnion implements BiPredicate<FixedLength.NaturalSequence, FixedLength.NaturalSequence> {
  private Predicate<Natural> predicate;
  
  public PredicatedUnion(Cipher cipher, Predicate<Natural> predicate){
    this.predicate = predicate;
  }
  
  @Override
  public boolean test(FixedLength.NaturalSequence a, FixedLength.NaturalSequence b) {
    return predicate.test(
        new Natural(
            Cipher.Name.Binary,
            Combination.merge(a.toBinaryNatural(), b.toBinaryNatural())
            )
        );
  }
}