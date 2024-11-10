package name.ncg777.Maths.Predicates;

import java.util.TreeMap;

import name.ncg777.CS.DataStructures.CollectionUtils;
import name.ncg777.CS.Functional.StandardAndGuavaPredicate;
import name.ncg777.Maths.Objects.Sequence;
import name.ncg777.Statistics.SpectrumMaximizePowersOfTwo;

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
