package name.ncg777.Music.PCS12Relations;

import name.ncg777.Maths.Relations.Relation;
import name.ncg777.Music.PCS12;

public class Different  implements Relation<PCS12, PCS12> {

  @Override
  public boolean apply(PCS12 a, PCS12 b) {
    return !a.equals(b);
  }

}
