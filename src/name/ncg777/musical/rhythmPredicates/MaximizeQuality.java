package name.ncg777.musical.rhythmPredicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.musical.Rhythm;
import name.ncg777.statistical.SpectrumMaximizeQuality;

public class MaximizeQuality  implements StandardAndGuavaPredicate<Rhythm>{

  @Override
  public boolean apply(@Nonnull Rhythm input) {
    return SpectrumMaximizeQuality.evaluate(Rhythm.calcSpectrum(input)) >= 0.0;
  }
}
