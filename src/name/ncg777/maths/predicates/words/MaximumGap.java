package name.ncg777.maths.predicates.words;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.objects.words.BinaryWord;

public class MaximumGap implements StandardAndGuavaPredicate<BinaryWord> {
  
  private int n = -1;
  public MaximumGap(int n) {
    if(n < 2) throw new RuntimeException("MaximumGap: invalid n value");
    this.n = n;
  }
  @Override
  public boolean apply(@Nonnull BinaryWord input) {
    return input.getComposition().asSequence().getMax() <= this.n;
  }

}
