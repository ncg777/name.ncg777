package name.ncg777.mathematics.relations;

import java.util.TreeSet;

import name.ncg777.mathematics.objects.Combination;

public class CombinationFlatSymmetricDifference implements Relation<Combination, Combination> {


  @Override
  public boolean apply(Combination a, Combination b) {
    Combination c = a.symmetricDifference(b);
    TreeSet<Integer> t = new TreeSet<Integer>();
    t.addAll(c.getIntervalVector());
    return t.size() == 1;
  }

}
