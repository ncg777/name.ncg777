package name.ncg777.maths.words.predicates;

import java.util.TreeMap;
import java.util.function.Predicate;

import name.ncg777.maths.Numbers;
import name.ncg777.maths.words.BinaryWord;

public class LowEntropy implements Predicate<BinaryWord> {
  private static TreeMap<Integer,Double> _cache = new TreeMap<>();
  
  public LowEntropy() {}
  @Override
  public boolean test(BinaryWord binaryWord) {
    var combination = binaryWord;
    Double bound = null;
    if(!_cache.containsKey(combination.getN())) {
      bound = Math.log((double)Numbers.reverseTriangularNumber(combination.getN())*0.5);
      _cache.put(combination.getN(), bound);
    } else {
      bound = _cache.get(combination.getN());
    }
    double h = combination.getComposition().asSequence().entropy();
    return h < bound;
  }

}
