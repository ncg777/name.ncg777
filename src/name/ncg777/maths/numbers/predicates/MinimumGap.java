package name.ncg777.maths.numbers.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.numbers.BinaryNatural;

public class MinimumGap implements StandardAndGuavaPredicate<BinaryNatural> {
  
  private int n = -1;
  public MinimumGap(int n) {
    this.n = n;
  }
  @Override
  public boolean apply(@Nonnull BinaryNatural input) {
    return input.getComposition().asSequence().getMin() >= this.n;
  }

}
