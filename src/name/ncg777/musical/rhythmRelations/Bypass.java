package name.ncg777.musical.rhythmRelations;

import name.ncg777.mathematics.relations.Relation;
import name.ncg777.musical.Rhythm;


public class Bypass implements 
Relation<Rhythm, Rhythm>   {
  
  @Override
  public boolean apply(Rhythm a, Rhythm b) {
    return true;
  }
}
