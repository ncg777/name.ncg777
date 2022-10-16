package name.NicolasCoutureGrenier.Music.SequencePredicates;

import java.util.Collection;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;
import name.NicolasCoutureGrenier.Music.Rhythm;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.ShadowContourIsomorphic;

public class SeqAllRhythmsSCI implements StandardAndGuavaPredicate<Sequence> {
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
