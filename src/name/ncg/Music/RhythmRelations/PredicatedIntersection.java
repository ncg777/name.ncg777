package name.ncg.Music.RhythmRelations;

import java.util.BitSet;

import com.google.common.base.Predicate;

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
    return ld.apply(Rhythm.buildRhythm(o, a.getN()));
  }
}
