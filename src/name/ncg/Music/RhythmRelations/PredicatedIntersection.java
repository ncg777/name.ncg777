package name.ncg.Music.RhythmRelations;

import java.util.BitSet;
import java.util.function.Predicate;

import name.ncg.Maths.Relations.Relation;
import name.ncg.Music.Rhythm;


public class PredicatedIntersection implements 
Relation<Rhythm, Rhythm>   {
  
  public PredicatedIntersection(Predicate<Rhythm> pred){
    ld = pred;
  }
  
  private Predicate<Rhythm> ld;
  @Override
  public boolean apply(Rhythm a, Rhythm b) {
    BitSet o = new BitSet(a.getN());
    o.or(a);
    o.and(b);
    return ld.test(Rhythm.buildRhythm(o, a.getN()));
  }
}
