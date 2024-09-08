package name.NicolasCoutureGrenier.Music.RhythmPredicates;

import javax.annotation.Nonnull;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Maths.Objects.Sequence;
import name.NicolasCoutureGrenier.Music.Rhythm;

public class ShadowContourIsomorphic implements StandardAndGuavaPredicate<Rhythm> {

  @Override
  public boolean apply(@Nonnull Rhythm input) {
    if(input.getK()<2) return true;
    return Sequence.equivalentUnderRotation(input.getContour(), input.getShadowContour()) || 
        Sequence.equivalentUnderRotation(input.getContour().reverse(), input.getShadowContour());
  }
}
