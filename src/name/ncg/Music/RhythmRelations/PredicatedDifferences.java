package name.ncg.Music.RhythmRelations;


import com.google.common.base.Predicate;

import name.ncg.Maths.Relations.Relation;
import name.ncg.Music.Rhythm;


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
    
    return ld.apply(d1) && ld.apply(d2); 
    
  }
}
