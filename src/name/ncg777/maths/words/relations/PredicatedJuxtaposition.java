package name.ncg777.maths.words.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.words.Alphabet;
import name.ncg777.maths.words.BinaryWord;
import name.ncg777.maths.words.Word;

public class PredicatedJuxtaposition implements BiPredicate<BinaryWord, BinaryWord>   {
  private Predicate<BinaryWord> predicate;
  
  public PredicatedJuxtaposition(Predicate<BinaryWord> predicate){
    this.predicate = predicate;
  }
  
  @Override
  public boolean test(BinaryWord a, BinaryWord b) {
    return predicate.test(
        Word.agglutinate(
            a.toWord(Alphabet.Name.Binary), 
            b.toWord(Alphabet.Name.Binary)).toBinaryWord());
  }
}