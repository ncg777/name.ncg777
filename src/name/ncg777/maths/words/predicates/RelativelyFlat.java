package name.ncg777.maths.words.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.Numbers;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.words.BinaryWord;

public class RelativelyFlat implements StandardAndGuavaPredicate<BinaryWord> {
  HasNoGaps h = new HasNoGaps();

  @Override
  public boolean apply(@Nonnull BinaryWord input) {
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
