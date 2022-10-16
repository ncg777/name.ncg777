package name.NicolasCoutureGrenier.Music.RhythmRelations;

import java.util.BitSet;

import name.NicolasCoutureGrenier.Maths.Relations.Relation;
import name.NicolasCoutureGrenier.Music.Rhythm;


public class EditDistanceSmallerThan implements 
Relation<Rhythm, Rhythm>   {
  private int _n = 2;
  public EditDistanceSmallerThan() {}
  public EditDistanceSmallerThan(int n) {
    _n = n;
  }
  @Override
  public boolean apply(Rhythm a, Rhythm b) {
    BitSet t = ((BitSet)a.clone());
    t.xor(b);
    return t.cardinality() < _n;
  }
}
