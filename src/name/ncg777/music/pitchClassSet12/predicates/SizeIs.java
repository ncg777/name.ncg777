package name.ncg777.music.pitchClassSet12.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.music.pitchClassSet12.PitchClassSet12;

public class SizeIs implements StandardAndGuavaPredicate<PitchClassSet12> {
  int m_n;

  public SizeIs(int p_n) {
    m_n = p_n;
  }

  public boolean apply(@Nonnull PitchClassSet12 input) {
    return input.getK() == m_n;
  }
}
