package name.ncg777.musical.pitchClassSet12Predicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.musical.pitchClassSet12;

public class SizeIs implements StandardAndGuavaPredicate<pitchClassSet12> {
  int m_n;

  public SizeIs(int p_n) {
    m_n = p_n;
  }

  public boolean apply(@Nonnull pitchClassSet12 input) {
    return input.getK() == m_n;
  }
}
