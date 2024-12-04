package name.ncg777.maths.words.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.objects.WordBinary;
import name.ncg777.statistics.SpectrumMaximizeQuality;

public class MaximizeQuality  implements StandardAndGuavaPredicate<WordBinary>{

  @Override
  public boolean apply(@Nonnull WordBinary input) {
    return SpectrumMaximizeQuality.evaluate(WordBinary.calcSpectrum(input)) >= 0.0;
  }
}
