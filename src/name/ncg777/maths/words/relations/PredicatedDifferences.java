package name.ncg777.maths.words.relations;

import java.util.function.Predicate;

import name.ncg777.maths.objects.WordBinary;
import name.ncg777.maths.relations.Relation;


public class PredicatedDifferences implements 
Relation<WordBinary, WordBinary>   {
  
  public PredicatedDifferences(Predicate<WordBinary> pred){
    ld = pred;
  }
  
  private Predicate<WordBinary> ld;
  @Override
  public boolean apply(WordBinary a, WordBinary b) {

    WordBinary d1 = WordBinary.buildRhythm(a.minus(b));
    WordBinary d2 = WordBinary.buildRhythm(b.minus(a));
    
    return ld.test(d1) && ld.test(d2); 
    
  }
}
