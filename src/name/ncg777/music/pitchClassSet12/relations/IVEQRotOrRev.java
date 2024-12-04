package name.ncg777.music.pitchClassSet12.relations;

import name.ncg777.maths.objects.Sequence;
import name.ncg777.maths.relations.Relation;
import name.ncg777.music.pitchClassSet12.PitchClassSet12;

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
