package name.ncg777.maths.numbers.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.numbers.Alphabet;
import name.ncg777.maths.numbers.BinaryNumber;
import name.ncg777.maths.numbers.Number;

public class PredicatedJuxtaposition implements BiPredicate<BinaryNumber, BinaryNumber>   {
  private Predicate<BinaryNumber> predicate;
  
  public PredicatedJuxtaposition(Predicate<BinaryNumber> predicate){
    this.predicate = predicate;
  }
  
  @Override
  public boolean test(BinaryNumber a, BinaryNumber b) {
    return predicate.test(
        Number.agglutinate(
            a.toWord(Alphabet.Name.Binary), 
            b.toWord(Alphabet.Name.Binary)).toBinaryWord());
  }
}