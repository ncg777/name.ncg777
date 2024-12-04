package name.ncg777.maths.relations;

import java.util.TreeSet;

import name.ncg777.maths.objects.Combination;

public class CombinationRelations {
  public Relation<Combination, Combination> combinationFlatSymmetricDifference = new Relation<Combination, Combination>() {
    @Override
    public boolean apply(Combination a, Combination b) {
      Combination c = a.symmetricDifference(b);
      TreeSet<Integer> t = new TreeSet<Integer>();
      t.addAll(c.getIntervalVector());
      return t.size() == 1;
    }
  };
}
