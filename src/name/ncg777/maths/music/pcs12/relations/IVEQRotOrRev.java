package name.ncg777.maths.music.pcs12.relations;

import name.ncg777.maths.music.pcs12.Pcs12;
import name.ncg777.maths.relations.Relation;
import name.ncg777.maths.sequences.Sequence;

public class IVEQRotOrRev implements Relation<Pcs12, Pcs12>  {

  @Override
  public boolean apply(Pcs12 a, Pcs12 b) {
    return 
        Sequence.equivalentUnderRotation(a.getIntervalVector(), 
            b.getIntervalVector()) || 
        Sequence.equivalentUnderRotation(a.getIntervalVector().reverse(), 
            b.getIntervalVector());
  }

}
