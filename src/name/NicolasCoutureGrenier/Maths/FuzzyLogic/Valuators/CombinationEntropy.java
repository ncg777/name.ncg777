package name.NicolasCoutureGrenier.Maths.FuzzyLogic.Valuators;

import java.util.TreeMap;

import com.google.common.base.Function;

import name.NicolasCoutureGrenier.Maths.Numbers;
import name.NicolasCoutureGrenier.Maths.DataStructures.Combination;
import name.NicolasCoutureGrenier.Maths.DataStructures.Interval;
import name.NicolasCoutureGrenier.Maths.FuzzyLogic.FuzzyVariable;

public class CombinationEntropy implements Function<Combination, FuzzyVariable> {

  private static TreeMap<Integer, Interval<Double>> minmax = new TreeMap<>();
  
  @Override
  public FuzzyVariable apply(Combination input) {
    int n = input.getN();
    
    double min = 0.0;
    double max = Double.MIN_VALUE;

    if (!minmax.containsKey(n)) {
      int k = Numbers.reverseTriangularNumber(n);
      max = Math.log(k);
      minmax.put(n, Interval.makeClosedInterval(0.0, max+0.0000000000000005));
    } else {
      min = minmax.get(n).getMinimum();
      max = minmax.get(n).getMaximum();
    }
    
    if (min == max) {
      return FuzzyVariable.from(0.5);
    } else {
      return FuzzyVariable.from((input.getComposition().asSequence().entropy() - min) / (max - min));
    }
  }


}
