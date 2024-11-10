package name.ncg777.Music.SequencePredicates;

import java.util.Collection;

import javax.annotation.Nonnull;

import name.ncg777.CS.Functional.StandardAndGuavaPredicate;
import name.ncg777.Maths.Objects.Sequence;
import name.ncg777.Music.Rhythm;
import name.ncg777.Music.RhythmPredicates.ShadowContourIsomorphic;

public class SeqAllRhythmsSCI implements StandardAndGuavaPredicate<Sequence> {
  ShadowContourIsomorphic sci = new ShadowContourIsomorphic();
  
  @Override
  public boolean apply(@Nonnull Sequence input) {
    Collection<Rhythm> rhythms = input.getRhythms().values();
    
    for(Rhythm r : rhythms) {
      if(!sci.apply(r)) return false;
    }
    return true;
  }
}
