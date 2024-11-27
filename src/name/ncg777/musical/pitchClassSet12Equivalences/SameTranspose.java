package name.ncg777.musical.pitchClassSet12Equivalences;

import javax.annotation.Nonnull;

import com.google.common.base.Equivalence;

import name.ncg777.musical.pitchClassSet12;

public class SameTranspose extends Equivalence<pitchClassSet12> {

  @Override
  protected boolean doEquivalent(@Nonnull pitchClassSet12 a, @Nonnull pitchClassSet12 b) {
    if (a == null || b == null) {
      return false;
    }
    return a.getTranspose().equals(b.getTranspose());
  }

  @Override
  protected int doHash(@Nonnull pitchClassSet12 t) {
    return t.getTranspose().hashCode();
  }

}
