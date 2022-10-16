package name.NicolasCoutureGrenier.Music.RhythmEquivalences;


import com.google.common.base.Equivalence;
import name.NicolasCoutureGrenier.Music.Rhythm16;

public class SamePhase16 extends Equivalence<Rhythm16> {

  @Override
  protected boolean doEquivalent(Rhythm16 a, Rhythm16 b) {
    if (a == null || b == null) {
      return false;
    }
    return a.getPhase().equals(b.getPhase());
  }

  @Override
  protected int doHash(Rhythm16 t) {
    if (t == null) {
      return 0;
    }
    return t.getPhase().hashCode();
  }

}
