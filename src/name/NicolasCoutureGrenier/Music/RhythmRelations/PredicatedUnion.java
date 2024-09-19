package name.NicolasCoutureGrenier.Music.RhythmRelations;

import java.util.BitSet;
import java.util.function.Predicate;

import name.NicolasCoutureGrenier.Maths.Relations.Relation;
import name.NicolasCoutureGrenier.Music.Rhythm;


public class PredicatedUnion implements 
Relation<Rhythm, Rhythm>   {
  
  public PredicatedUnion(Predicate<Rhythm> pred){
    ld = pred;
  }
  
  private Predicate<Rhythm> ld;
  @Override
  public boolean apply(Rhythm a, Rhythm b) {
    BitSet o = new BitSet(a.getN());
    o.or(a);
    o.or(b);
    return ld.test(Rhythm.buildRhythm(o, a.getN()));
  }
}
