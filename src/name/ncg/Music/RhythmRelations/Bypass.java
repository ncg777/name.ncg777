package name.ncg.Music.RhythmRelations;

import name.ncg.Maths.Relations.Relation;
import name.ncg.Music.Rhythm;


public class Bypass implements 
Relation<Rhythm, Rhythm>   {
  
  @Override
  public boolean apply(Rhythm a, Rhythm b) {
    return true;
  }
}
