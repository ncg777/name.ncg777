package name.ncg777.music.pitchClassSet12Relations;

import name.ncg777.maths.relations.Relation;
import name.ncg777.music.PitchClassSet12;

public class Different  implements Relation<PitchClassSet12, PitchClassSet12> {

  @Override
  public boolean apply(PitchClassSet12 a, PitchClassSet12 b) {
    return !a.equals(b);
  }

}
