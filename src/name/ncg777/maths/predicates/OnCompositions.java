package name.ncg777.maths.predicates;

import java.util.function.Predicate;

import name.ncg777.maths.Composition;
import name.ncg777.maths.Numbers;

public class OnCompositions {
  public static Predicate<Composition> duplePartitioned = (Composition _c) -> {
    var c = _c.asSequence();
      int acc = c.get(0);
      for(int i=1;i<c.size();i++) {
        if(!Numbers.isPowerOfTwo(acc)) {
          if(c.get(i) > c.get(i-1)) return false;
        }
        acc += c.get(i);
      }
      return true;
  };
}
