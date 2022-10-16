package name.ncg.Maths.Predicates;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Maths.Relations.Relation;

public class BoundRelationSecond<T extends Comparable<? super T>, U extends Comparable<? super U>>
    implements
    StandardAndGuavaPredicate<T> {
  Relation<T, U> r;
  U b;

  public BoundRelationSecond(Relation<T, U> r, U b) {
    this.b = b;
    this.r = r;
  }


  @Override
  public boolean apply(T input) {
    return r.apply(input, b);
  }

}
