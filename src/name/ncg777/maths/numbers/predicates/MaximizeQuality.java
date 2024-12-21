package name.ncg777.maths.numbers.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.numbers.BinaryNumber;
import name.ncg777.statistics.SpectrumMaximizeQuality;

public class MaximizeQuality  implements StandardAndGuavaPredicate<BinaryNumber>{

  @Override
  public boolean apply(@Nonnull BinaryNumber input) {
    return SpectrumMaximizeQuality.evaluate(BinaryNumber.calcSpectrum(input)) >= 0.0;
  }
}
