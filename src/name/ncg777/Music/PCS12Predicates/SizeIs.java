package name.ncg777.Music.PCS12Predicates;

import javax.annotation.Nonnull;

import name.ncg777.CS.Functional.StandardAndGuavaPredicate;
import name.ncg777.Music.PCS12;

public class SizeIs implements StandardAndGuavaPredicate<PCS12> {
  int m_n;

  public SizeIs(int p_n) {
    m_n = p_n;
  }

  public boolean apply(@Nonnull PCS12 input) {
    return input.getK() == m_n;
  }
}
