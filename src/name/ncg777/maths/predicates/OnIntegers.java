package name.ncg777.maths.predicates;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.function.Predicate;

public class OnIntegers {
  public static Predicate<Integer> multiplesOfAny(TreeSet<Integer> p_t)  {
    return new Predicate<Integer>() {
      @Override
      public boolean test(Integer k) {
        Iterator<Integer> j = p_t.iterator();
        while (j.hasNext()) {
          if (k % j.next() == 0) {
            return true;
          }
        }
        return false;
      }
    };
  };
}
