package name.ncg777.maths.music.pcs12.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.music.pcs12.Pcs12;
import name.ncg777.maths.sequences.Sequence;

public class FifthMax implements StandardAndGuavaPredicate<Pcs12>   {

  @Override
  public boolean apply(@Nonnull Pcs12 arg0) {
    Sequence s = arg0.getIntervalVector();
    
    return s.get(4).equals(s.getMax());
  }

}
