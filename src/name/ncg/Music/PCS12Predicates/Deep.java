package name.ncg.Music.PCS12Predicates;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.PCS12;

public class Deep implements StandardAndGuavaPredicate<PCS12>  {

  @Override
  public boolean apply(PCS12 arg0) {
    Sequence s = arg0.getIntervalVector();
    
    return s.getMin() == s.getMax();
  }

}
