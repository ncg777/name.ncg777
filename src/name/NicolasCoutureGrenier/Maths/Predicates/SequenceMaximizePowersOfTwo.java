package name.NicolasCoutureGrenier.Maths.Predicates;

import java.util.TreeMap;

import name.NicolasCoutureGrenier.CS.DataStructures.CollectionUtils;
import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Maths.Objects.Sequence;
import name.NicolasCoutureGrenier.Statistics.SpectrumMaximizePowersOfTwo;

public class SequenceMaximizePowersOfTwo implements StandardAndGuavaPredicate<Sequence>{

  @Override
  public boolean apply(Sequence input) {
    TreeMap<Integer, Sequence> v = CollectionUtils.calcIntervalVector(input);
    for(Integer i : v.keySet()) {
      if(SpectrumMaximizePowersOfTwo.evaluate(v.get(i)) < 0.0) return false; 
    }
    
    return true;
  }
  
}
