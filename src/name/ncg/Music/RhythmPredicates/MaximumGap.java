package name.ncg.Music.RhythmPredicates;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Music.Rhythm;

public class MaximumGap implements StandardAndGuavaPredicate<Rhythm> {
  
  private int n = -1;
  public MaximumGap(int n) {
    if(n < 2) throw new RuntimeException("MaximumGap: invalid n value");
    this.n = n;
  }
  @Override
  public boolean apply(Rhythm input) {
    return input.getComposition().asSequence().getMax() <= this.n;
  }

}
