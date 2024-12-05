package name.ncg777.maths.pitchClassSet12.relations;

import name.ncg777.maths.pitchClassSet12.PitchClassSet12;
import name.ncg777.maths.relations.Relation;

public class Different  implements Relation<PitchClassSet12, PitchClassSet12> {

  @Override
  public boolean apply(PitchClassSet12 a, PitchClassSet12 b) {
    return !a.equals(b);
  }

}
