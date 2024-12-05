package name.ncg777.maths.pitchClassSet12.relations;

import javax.annotation.Nonnull;

import com.google.common.base.Equivalence;

import name.ncg777.maths.pitchClassSet12.PitchClassSet12;

public class SameTranspose extends Equivalence<PitchClassSet12> {

  @Override
  protected boolean doEquivalent(@Nonnull PitchClassSet12 a, @Nonnull PitchClassSet12 b) {
    if (a == null || b == null) {
      return false;
    }
    return a.getTranspose().equals(b.getTranspose());
  }

  @Override
  protected int doHash(@Nonnull PitchClassSet12 t) {
    return t.getTranspose().hashCode();
  }

}
