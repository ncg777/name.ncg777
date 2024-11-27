package name.ncg777.musical.rhythmPredicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.musical.Rhythm;

public class SecondOrderDifferenceSum implements StandardAndGuavaPredicate<Rhythm>{
  public static enum Keep {
    Negative,
    Positive,
    Zero
  }
  private Keep keep = null;
  public SecondOrderDifferenceSum(Keep keep) {
    if(keep == null) throw new RuntimeException("keep cannot be null");
    this.keep = keep;
  }

  @Override
  public boolean apply(@Nonnull Rhythm input) {
    int sum = input.getComposition().asSequence().cyclicalDifference().cyclicalDifference().sum();    
    
    switch(keep) {
      case Zero: return sum == 0;
      case Negative : return sum < 0;
      case Positive: return sum > 0;
    }
    
    throw new RuntimeException("this should never happen");
  }

}
