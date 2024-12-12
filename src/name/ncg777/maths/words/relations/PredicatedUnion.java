package name.ncg777.maths.words.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.Combination;
import name.ncg777.maths.words.Alphabet;
import name.ncg777.maths.words.QuartalWord;
import name.ncg777.maths.words.Word;

public class PredicatedUnion implements BiPredicate<QuartalWord, QuartalWord> {
  private Predicate<Word> predicate;
  
  public PredicatedUnion(Alphabet alphabet, Predicate<Word> predicate){
    this.predicate = predicate;
  }
  
  @Override
  public boolean test(QuartalWord a, QuartalWord b) {
    return predicate.test(
        new Word(
            Alphabet.Name.Binary,
            Combination.merge(a.toBinaryWord(), b.toBinaryWord())
            )
        );
  }
}