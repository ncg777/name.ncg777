package name.ncg777.maths.words.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.words.Word;


public class PredicatedJuxtaposition implements BiPredicate<Word, Word>   {
  private Predicate<Word> predicate;
  
  public PredicatedJuxtaposition(Predicate<Word> predicate){
    this.predicate = predicate;
  }
  
  @Override
  public boolean test(Word a, Word b) {
    return predicate.test(Word.agglutinate(a, b));
  }
}