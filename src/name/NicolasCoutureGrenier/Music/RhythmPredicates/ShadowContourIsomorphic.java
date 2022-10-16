package name.NicolasCoutureGrenier.Music.RhythmPredicates;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;
import name.NicolasCoutureGrenier.Music.Rhythm;

public class ShadowContourIsomorphic implements StandardAndGuavaPredicate<Rhythm> {

  @Override
  public boolean apply(Rhythm input) {
    if(input.getK()<2) return true;
    return Sequence.equivalentUnderRotation(input.getContour(), input.getShadowContour());
  }
}
