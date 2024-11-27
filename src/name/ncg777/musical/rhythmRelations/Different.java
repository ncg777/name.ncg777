package name.ncg777.musical.rhythmRelations;

import name.ncg777.mathematics.relations.Relation;
import name.ncg777.musical.Rhythm;


public class Different implements Relation<Rhythm, Rhythm>  {

  @Override
  public boolean apply(Rhythm a, Rhythm b) {
    return !a.equals(b);
  }

}
