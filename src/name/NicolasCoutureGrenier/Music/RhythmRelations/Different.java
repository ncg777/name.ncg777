package name.NicolasCoutureGrenier.Music.RhythmRelations;

import name.NicolasCoutureGrenier.Maths.Relations.Relation;
import name.NicolasCoutureGrenier.Music.Rhythm;


public class Different implements Relation<Rhythm, Rhythm>  {

  @Override
  public boolean apply(Rhythm a, Rhythm b) {
    return !a.equals(b);
  }

}
