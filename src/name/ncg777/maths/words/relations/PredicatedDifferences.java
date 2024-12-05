package name.ncg777.maths.words.relations;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.words.Alphabet;
import name.ncg777.maths.words.Tetragraph;
import name.ncg777.maths.words.Word;


public class PredicatedDifferences implements BiPredicate<Tetragraph, Tetragraph> {
  private Predicate<Word> predicate;
  
  public PredicatedDifferences(Predicate<Word> predicate){
    this.predicate = predicate;
  }
  
  @Override
  public boolean test(Tetragraph a, Tetragraph b) {
    var ac = a.toBinaryWord();
    var bc = b.toBinaryWord();
    
    var d1 = new Word(Alphabet.Binary, ac.minus(bc));
    var d2 = new Word(Alphabet.Binary, bc.minus(ac));
    
    return predicate.test(d1) && predicate.test(d2); 
  }
}
