package name.ncg777.musical.pitchClassSet12Equivalences;

import javax.annotation.Nonnull;

import com.google.common.base.Equivalence;

import name.ncg777.musical.PitchClassSet12;

public class SameIntervalVector extends Equivalence<PitchClassSet12> {

  @Override
  protected boolean doEquivalent(@Nonnull PitchClassSet12 a, @Nonnull PitchClassSet12 b) {
    if (a == null || b == null) {
      return false;
    }
    return a.getIntervalVector().equals(b.getIntervalVector());
  }

  @Override
  protected int doHash(@Nonnull PitchClassSet12 t) {
    return t.getIntervalVector().hashCode();
  }

}
