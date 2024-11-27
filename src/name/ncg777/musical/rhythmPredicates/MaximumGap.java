package name.ncg777.musical.rhythmPredicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.musical.Rhythm;

public class MaximumGap implements StandardAndGuavaPredicate<Rhythm> {
  
  private int n = -1;
  public MaximumGap(int n) {
    if(n < 2) throw new RuntimeException("MaximumGap: invalid n value");
    this.n = n;
  }
  @Override
  public boolean apply(@Nonnull Rhythm input) {
    return input.getComposition().asSequence().getMax() <= this.n;
  }

}
