package name.ncg777.Maths.FuzzyLogic.Valuators;

import java.util.TreeMap;

import com.google.common.base.Function;

import name.ncg777.Maths.Numbers;
import name.ncg777.Maths.FuzzyLogic.FuzzyVariable;
import name.ncg777.Maths.Objects.Combination;
import name.ncg777.Maths.Objects.Interval;

public class CombinationEntropy implements Function<Combination, FuzzyVariable> {

  private static TreeMap<Integer, Interval<Double>> minmax = new TreeMap<>();
  
  @Override
  public FuzzyVariable apply(Combination input) {
    int n = input.getN();
    
    double min = 0.0;
    double max = Double.MIN_VALUE;

    if (!minmax.containsKey(n)) {
      long k = Numbers.reverseTriangularNumber(n);
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
