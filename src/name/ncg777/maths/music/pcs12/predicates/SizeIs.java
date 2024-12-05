package name.ncg777.maths.music.pcs12.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.music.pcs12.Pcs12;

public class SizeIs implements StandardAndGuavaPredicate<Pcs12> {
  int m_n;

  public SizeIs(int p_n) {
    m_n = p_n;
  }

  public boolean apply(@Nonnull Pcs12 input) {
    return input.getK() == m_n;
  }
}
