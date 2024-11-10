package name.ncg777.Music.PCS12Predicates;

import javax.annotation.Nonnull;

import name.ncg777.CS.Functional.StandardAndGuavaPredicate;
import name.ncg777.Maths.Objects.Sequence;
import name.ncg777.Music.PCS12;

public class FifthMax implements StandardAndGuavaPredicate<PCS12>   {

  @Override
  public boolean apply(@Nonnull PCS12 arg0) {
    Sequence s = arg0.getIntervalVector();
    
    return s.get(4).equals(s.getMax());
  }

}
