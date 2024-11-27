package name.ncg777.musical.rhythmPredicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.musical.Rhythm;

public class MinimumGap implements StandardAndGuavaPredicate<Rhythm> {
  
  private int n = -1;
  public MinimumGap(int n) {
    this.n = n;
  }
  @Override
  public boolean apply(@Nonnull Rhythm input) {
    return input.getComposition().asSequence().getMin() >= this.n;
  }

}
