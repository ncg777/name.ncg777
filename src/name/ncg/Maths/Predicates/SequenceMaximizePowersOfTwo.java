package name.ncg.Maths.Predicates;

import java.util.TreeMap;

import com.google.common.base.Predicate;

import name.ncg.Maths.DataStructures.CollectionUtils;
import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Statistics.SpectrumMaximizePowersOfTwo;

public class SequenceMaximizePowersOfTwo implements Predicate<Sequence>{

  @Override
  public boolean apply(Sequence input) {
    TreeMap<Integer, Sequence> v = CollectionUtils.calcIntervalVector(input);
    for(Integer i : v.keySet()) {
      if(SpectrumMaximizePowersOfTwo.evaluate(v.get(i)) < 0.0) return false; 
    }
    
    return true;
  }
  
}
