package name.ncg777.musical.pitchClassSet12Relations;

import name.ncg777.mathematics.objects.Sequence;
import name.ncg777.mathematics.relations.Relation;
import name.ncg777.musical.PitchClassSet12;

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
