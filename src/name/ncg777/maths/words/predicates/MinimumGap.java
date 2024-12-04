package name.ncg777.maths.words.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.objects.WordBinary;

public class MinimumGap implements StandardAndGuavaPredicate<WordBinary> {
  
  private int n = -1;
  public MinimumGap(int n) {
    this.n = n;
  }
  @Override
  public boolean apply(@Nonnull WordBinary input) {
    return input.getComposition().asSequence().getMin() >= this.n;
  }

}
