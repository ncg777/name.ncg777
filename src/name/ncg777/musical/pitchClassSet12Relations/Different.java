package name.ncg777.musical.pitchClassSet12Relations;

import name.ncg777.mathematics.relations.Relation;
import name.ncg777.musical.pitchClassSet12;

public class Different  implements Relation<pitchClassSet12, pitchClassSet12> {

  @Override
  public boolean apply(pitchClassSet12 a, pitchClassSet12 b) {
    return !a.equals(b);
  }

}
