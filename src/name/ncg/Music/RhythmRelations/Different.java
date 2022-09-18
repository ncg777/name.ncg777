package name.ncg.Music.RhythmRelations;

import name.ncg.Maths.Relations.Relation;
import name.ncg.Music.Rhythm;


public class Different implements Relation<Rhythm, Rhythm>  {

  @Override
  public boolean apply(Rhythm a, Rhythm b) {
    return !a.equals(b);
  }

}
