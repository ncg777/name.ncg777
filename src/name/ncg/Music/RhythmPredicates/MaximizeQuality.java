package name.ncg.Music.RhythmPredicates;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Music.Rhythm;
import name.ncg.Statistics.SpectrumMaximizeQuality;

public class MaximizeQuality  implements StandardAndGuavaPredicate<Rhythm>{

  @Override
  public boolean apply(Rhythm input) {
    return SpectrumMaximizeQuality.evaluate(Rhythm.calcSpectrum(input)) >= 0.0;
  }
}
