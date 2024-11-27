package name.ncg777.musical.rhythmPredicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.mathematics.Numbers;
import name.ncg777.mathematics.objects.Sequence;
import name.ncg777.musical.Rhythm;

public class RelativelyFlat implements StandardAndGuavaPredicate<Rhythm> {
  HasNoGaps h = new HasNoGaps();

  @Override
  public boolean apply(@Nonnull Rhythm input) {
    if (!h.apply(input)) {
      return false;
    }

    Sequence a = input.getIntervalVector();
    a.removeIf((v) -> v == 0);
    
    int n = a.size();
    
    double mean = Numbers.triangularNumber(input.getK()) / (double) n;

    for (int i = 0; i < a.size(); i++) {
      if(Math.abs(((double) a.get(i)) - mean) > 0.5*mean) {return false;}
    }
    return true;
  }


}
