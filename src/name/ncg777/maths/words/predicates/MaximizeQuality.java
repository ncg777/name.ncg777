package name.ncg777.maths.words.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.words.BinaryWord;
import name.ncg777.statistics.SpectrumMaximizeQuality;

public class MaximizeQuality  implements StandardAndGuavaPredicate<BinaryWord>{

  @Override
  public boolean apply(@Nonnull BinaryWord input) {
    return SpectrumMaximizeQuality.evaluate(BinaryWord.calcSpectrum(input)) >= 0.0;
  }
}
