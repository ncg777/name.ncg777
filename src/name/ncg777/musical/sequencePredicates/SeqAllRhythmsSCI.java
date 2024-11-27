package name.ncg777.musical.sequencePredicates;

import java.util.Collection;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.mathematics.objects.Sequence;
import name.ncg777.musical.Rhythm;
import name.ncg777.musical.rhythmPredicates.ShadowContourIsomorphic;

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
