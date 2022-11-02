package name.NicolasCoutureGrenier.Music.RhythmPredicates;

import java.util.TreeMap;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Maths.Numbers;
import name.NicolasCoutureGrenier.Music.Rhythm;

public class LowEntropy implements StandardAndGuavaPredicate<Rhythm> {
  private static TreeMap<Integer,Double> _cache = new TreeMap<Integer,Double>();
  @Override
  public boolean apply(Rhythm arg0) {
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
