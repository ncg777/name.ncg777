package name.ncg777.maths.words.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.objects.WordBinary;

public class MaximumGap implements StandardAndGuavaPredicate<WordBinary> {
  
  private int n = -1;
  public MaximumGap(int n) {
    if(n < 2) throw new RuntimeException("MaximumGap: invalid n value");
    this.n = n;
  }
  @Override
  public boolean apply(@Nonnull WordBinary input) {
    return input.getComposition().asSequence().getMax() <= this.n;
  }

}
