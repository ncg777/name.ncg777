package name.ncg777.maths.predicates.words;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.objects.words.BinaryWord;
import name.ncg777.statistics.SpectrumMaximizeQuality;

public class MaximizeQuality  implements StandardAndGuavaPredicate<BinaryWord>{

  @Override
  public boolean apply(@Nonnull BinaryWord input) {
    return SpectrumMaximizeQuality.evaluate(BinaryWord.calcSpectrum(input)) >= 0.0;
  }
}
