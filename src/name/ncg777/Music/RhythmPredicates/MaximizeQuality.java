package name.ncg777.Music.RhythmPredicates;

import javax.annotation.Nonnull;

import name.ncg777.CS.Functional.StandardAndGuavaPredicate;
import name.ncg777.Music.Rhythm;
import name.ncg777.Statistics.SpectrumMaximizeQuality;

public class MaximizeQuality  implements StandardAndGuavaPredicate<Rhythm>{

  @Override
  public boolean apply(@Nonnull Rhythm input) {
    return SpectrumMaximizeQuality.evaluate(Rhythm.calcSpectrum(input)) >= 0.0;
  }
}
