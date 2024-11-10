package name.ncg777.Maths.Relations;

import java.util.TreeSet;

import name.ncg777.Maths.Objects.Combination;

public class CombinationFlatSymmetricDifference implements Relation<Combination, Combination> {


  @Override
  public boolean apply(Combination a, Combination b) {
    Combination c = a.symmetricDifference(b);
    TreeSet<Integer> t = new TreeSet<Integer>();
    t.addAll(c.getIntervalVector());
    return t.size() == 1;
  }

}
