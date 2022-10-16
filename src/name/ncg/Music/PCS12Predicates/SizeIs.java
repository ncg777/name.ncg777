package name.ncg.Music.PCS12Predicates;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Music.PCS12;

public class SizeIs implements StandardAndGuavaPredicate<PCS12> {
  int m_n;

  public SizeIs(int p_n) {
    m_n = p_n;
  }

  public boolean apply(PCS12 input) {
    return input.getK() == m_n;
  }
}
