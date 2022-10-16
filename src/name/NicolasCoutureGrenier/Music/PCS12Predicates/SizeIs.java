package name.NicolasCoutureGrenier.Music.PCS12Predicates;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Music.PCS12;

public class SizeIs implements StandardAndGuavaPredicate<PCS12> {
  int m_n;

  public SizeIs(int p_n) {
    m_n = p_n;
  }

  public boolean apply(PCS12 input) {
    return input.getK() == m_n;
  }
}
