package name.ncg777.maths.music.pcs12.relations;

import javax.annotation.Nonnull;

import com.google.common.base.Equivalence;

import name.ncg777.maths.music.pcs12.Pcs12;

public class SameTranspose extends Equivalence<Pcs12> {

  @Override
  protected boolean doEquivalent(@Nonnull Pcs12 a, @Nonnull Pcs12 b) {
    if (a == null || b == null) {
      return false;
    }
    return a.getTranspose().equals(b.getTranspose());
  }

  @Override
  protected int doHash(@Nonnull Pcs12 t) {
    return t.getTranspose().hashCode();
  }

}
