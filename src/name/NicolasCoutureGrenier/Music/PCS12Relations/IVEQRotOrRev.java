package name.NicolasCoutureGrenier.Music.PCS12Relations;

import name.NicolasCoutureGrenier.Maths.Objects.Sequence;
import name.NicolasCoutureGrenier.Maths.Relations.Relation;
import name.NicolasCoutureGrenier.Music.PCS12;

public class IVEQRotOrRev implements Relation<PCS12, PCS12>  {

  @Override
  public boolean apply(PCS12 a, PCS12 b) {
    return 
        Sequence.equivalentUnderRotation(a.getIntervalVector(), 
            b.getIntervalVector()) || 
        Sequence.equivalentUnderRotation(a.getIntervalVector().reverse(), 
            b.getIntervalVector());
  }

}
