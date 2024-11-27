package name.ncg777.mathematics.predicates;

import java.util.TreeMap;

import name.ncg777.computerScience.dataStructures.CollectionUtils;
import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.mathematics.objects.Sequence;
import name.ncg777.statistical.SpectrumMaximizePowersOfTwo;

public class SequenceMaximizePowersOfTwo implements StandardAndGuavaPredicate<Sequence>{

  @SuppressWarnings("null")
  @Override
  public boolean apply(Sequence input) {
    TreeMap<Integer, Sequence> v = CollectionUtils.calcIntervalVector(input);
    for(Integer i : v.keySet()) {
      if(SpectrumMaximizePowersOfTwo.evaluate(v.get(i)) < 0.0) return false; 
    }
    
    return true;
  }
  
}
