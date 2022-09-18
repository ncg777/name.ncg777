package name.ncg.Music.SequencePredicates;

import com.google.common.base.Predicate;
import java.util.Collection;

import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.Rhythm;
import name.ncg.Music.RhythmPredicates.MaximizeQuality;


public class SeqAllRhythmsMaximizeQuality implements Predicate<Sequence> {
  private MaximizeQuality pred = new MaximizeQuality();

  @Override
  public boolean apply(Sequence input) {
    Collection<Rhythm> t = input.getRhythms().values();
    
    for(Rhythm r : t) {
      if(!pred.apply(r)) return false;
    }
    return true;
  }

}
