package name.ncg777.Music.PCS12Equivalences;

import javax.annotation.Nonnull;

import com.google.common.base.Equivalence;

import name.ncg777.Music.PCS12;

public class SameIntervalVector extends Equivalence<PCS12> {

  @Override
  protected boolean doEquivalent(@Nonnull PCS12 a, @Nonnull PCS12 b) {
    if (a == null || b == null) {
      return false;
    }
    return a.getIntervalVector().equals(b.getIntervalVector());
  }

  @Override
  protected int doHash(@Nonnull PCS12 t) {
    return t.getIntervalVector().hashCode();
  }

}
