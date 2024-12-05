package name.ncg777.maths.pitchClassSet12.relations;

import name.ncg777.maths.pitchClassSet12.PitchClassSet12;
import name.ncg777.maths.relations.Relation;
import name.ncg777.maths.sequences.Sequence;

public class IVEQRotOrRev implements Relation<PitchClassSet12, PitchClassSet12>  {

  @Override
  public boolean apply(PitchClassSet12 a, PitchClassSet12 b) {
    return 
        Sequence.equivalentUnderRotation(a.getIntervalVector(), 
            b.getIntervalVector()) || 
        Sequence.equivalentUnderRotation(a.getIntervalVector().reverse(), 
            b.getIntervalVector());
  }

}
