package name.ncg777.maths.music.pcs12.relations;

import name.ncg777.maths.music.pcs12.Pcs12;
import name.ncg777.maths.relations.Relation;

public class Different  implements Relation<Pcs12, Pcs12> {

  @Override
  public boolean apply(Pcs12 a, Pcs12 b) {
    return !a.equals(b);
  }

}
