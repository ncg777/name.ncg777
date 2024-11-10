package name.ncg777.Music.RhythmRelations;

import name.ncg777.Maths.Relations.Relation;
import name.ncg777.Music.Rhythm;


public class Different implements Relation<Rhythm, Rhythm>  {

  @Override
  public boolean apply(Rhythm a, Rhythm b) {
    return !a.equals(b);
  }

}
