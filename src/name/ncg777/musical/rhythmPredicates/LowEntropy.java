package name.ncg777.musical.rhythmPredicates;

import java.util.TreeMap;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.mathematics.Numbers;
import name.ncg777.musical.Rhythm;

public class LowEntropy implements StandardAndGuavaPredicate<Rhythm> {
  private static TreeMap<Integer,Double> _cache = new TreeMap<Integer,Double>();
  @Override
  public boolean apply(@Nonnull Rhythm arg0) {
    Double bound = null;
    if(!_cache.containsKey(arg0.getN())) {
      bound = Math.log((double)Numbers.reverseTriangularNumber(arg0.getN())*0.5);
      _cache.put(arg0.getN(), bound);
    } else {
      bound = _cache.get(arg0.getN());
    }
    double h = arg0.getComposition().asSequence().entropy();
    return h < bound;
  }

}
