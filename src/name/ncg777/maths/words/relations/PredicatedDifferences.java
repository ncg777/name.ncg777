package name.ncg777.maths.words.relations;

import java.util.function.Predicate;

import name.ncg777.maths.objects.words.BinaryWord;
import name.ncg777.maths.relations.Relation;


public class PredicatedDifferences implements 
Relation<BinaryWord, BinaryWord>   {
  
  public PredicatedDifferences(Predicate<BinaryWord> pred){
    ld = pred;
  }
  
  private Predicate<BinaryWord> ld;
  @Override
  public boolean apply(BinaryWord a, BinaryWord b) {

    BinaryWord d1 = BinaryWord.buildRhythm(a.minus(b));
    BinaryWord d2 = BinaryWord.buildRhythm(b.minus(a));
    
    return ld.test(d1) && ld.test(d2); 
    
  }
}
