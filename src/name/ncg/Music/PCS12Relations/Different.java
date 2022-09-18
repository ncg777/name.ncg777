package name.ncg.Music.PCS12Relations;

import name.ncg.Maths.Relations.Relation;
import name.ncg.Music.PCS12;

public class Different  implements Relation<PCS12, PCS12> {

  @Override
  public boolean apply(PCS12 a, PCS12 b) {
    return !a.equals(b);
  }

}
