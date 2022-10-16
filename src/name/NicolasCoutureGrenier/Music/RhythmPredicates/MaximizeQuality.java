package name.NicolasCoutureGrenier.Music.RhythmPredicates;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Music.Rhythm;
import name.NicolasCoutureGrenier.Statistics.SpectrumMaximizeQuality;

public class MaximizeQuality  implements StandardAndGuavaPredicate<Rhythm>{

  @Override
  public boolean apply(Rhythm input) {
    return SpectrumMaximizeQuality.evaluate(Rhythm.calcSpectrum(input)) >= 0.0;
  }
}
