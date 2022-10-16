package name.NicolasCoutureGrenier.Music.PCS12Equivalences;

import com.google.common.base.Equivalence;
import name.NicolasCoutureGrenier.Music.PCS12;

public class SameIntervalVector extends Equivalence<PCS12> {

  @Override
  protected boolean doEquivalent(PCS12 a, PCS12 b) {
    if (a == null || b == null) {
      return false;
    }
    return a.getIntervalVector().equals(b.getIntervalVector());
  }

  @Override
  protected int doHash(PCS12 t) {
    if (t == null) {
      return 0;
    }
    return t.getIntervalVector().hashCode();
  }

}
