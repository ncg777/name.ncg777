package name.ncg.Music.SequencePredicates;

import com.google.common.base.Predicate;
import java.util.Collection;

import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.Rhythm;
import name.ncg.Music.RhythmPredicates.ShadowContourIsomorphic;

public class SeqAllRhythmsSCI implements Predicate<Sequence> {
  ShadowContourIsomorphic sci = new ShadowContourIsomorphic();
  
  @Override
  public boolean apply(Sequence input) {
    Collection<Rhythm> rhythms = input.getRhythms().values();
    
    for(Rhythm r : rhythms) {
      if(!sci.apply(r)) return false;
    }
    return true;
  }
}
