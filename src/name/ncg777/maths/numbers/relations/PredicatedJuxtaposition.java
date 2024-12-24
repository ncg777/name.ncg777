package name.ncg777.maths.numbers.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.numbers.Natural;

public class PredicatedJuxtaposition implements BiPredicate<BinaryNatural, BinaryNatural>   {
  private Predicate<BinaryNatural> predicate;
  
  public PredicatedJuxtaposition(Predicate<BinaryNatural> predicate){
    this.predicate = predicate;
  }
  
  @Override
  public boolean test(BinaryNatural a, BinaryNatural b) {
    return predicate.test(
        Natural.agglutinate(
            a.toNatural(Cipher.Name.Binary), 
            b.toNatural(Cipher.Name.Binary)).toBinaryWord());
  }
}