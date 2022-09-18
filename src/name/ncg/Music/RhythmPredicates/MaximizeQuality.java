package name.ncg.Music.RhythmPredicates;

import com.google.common.base.Predicate;

import name.ncg.Music.Rhythm;
import name.ncg.Statistics.SpectrumMaximizeQuality;

public class MaximizeQuality  implements Predicate<Rhythm>{

  @Override
  public boolean apply(Rhythm input) {
    return SpectrumMaximizeQuality.evaluate(Rhythm.calcSpectrum(input)) >= 0.0;
  }
}
