package name.ncg777.maths.numbers.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.statistics.SpectrumMaximizeQuality;

public class MaximizeQuality  implements StandardAndGuavaPredicate<BinaryNatural>{

  @Override
  public boolean apply(@Nonnull BinaryNatural input) {
    return SpectrumMaximizeQuality.evaluate(BinaryNatural.calcSpectrum(input)) >= 0.0;
  }
}
