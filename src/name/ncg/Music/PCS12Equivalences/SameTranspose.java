package name.ncg.Music.PCS12Equivalences;

import com.google.common.base.Equivalence;
import name.ncg.Music.PCS12;

public class SameTranspose extends Equivalence<PCS12> {

  @Override
  protected boolean doEquivalent(PCS12 a, PCS12 b) {
    if (a == null || b == null) {
      return false;
    }
    return a.getTranspose().equals(b.getTranspose());
  }

  @Override
  protected int doHash(PCS12 t) {
    if (t == null) {
      return 0;
    }
    return t.getTranspose().hashCode();
  }

}
