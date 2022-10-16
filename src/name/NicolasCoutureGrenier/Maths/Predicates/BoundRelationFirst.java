package name.NicolasCoutureGrenier.Maths.Predicates;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Maths.Relations.Relation;

public class BoundRelationFirst<T extends Comparable<? super T>, U extends Comparable<? super U>>
    implements
      StandardAndGuavaPredicate<U> {
  Relation<T, U> r;
  T b;

  public BoundRelationFirst(T b, Relation<T, U> r) {
    this.b = b;
    this.r = r;
  }


  @Override
  public boolean apply(U input) {
    return r.apply(b, input);
  }

}
