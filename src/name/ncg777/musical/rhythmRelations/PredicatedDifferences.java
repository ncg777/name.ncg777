package name.ncg777.musical.rhythmRelations;

import java.util.function.Predicate;

import name.ncg777.mathematics.relations.Relation;
import name.ncg777.musical.Rhythm;


public class PredicatedDifferences implements 
Relation<Rhythm, Rhythm>   {
  
  public PredicatedDifferences(Predicate<Rhythm> pred){
    ld = pred;
  }
  
  private Predicate<Rhythm> ld;
  @Override
  public boolean apply(Rhythm a, Rhythm b) {

    Rhythm d1 = Rhythm.buildRhythm(a.minus(b));
    Rhythm d2 = Rhythm.buildRhythm(b.minus(a));
    
    return ld.test(d1) && ld.test(d2); 
    
  }
}
