package name.ncg777.maths.relations;

import java.util.Set;
import java.util.function.BiPredicate;

public class SetRelations {
  public static BiPredicate<Set<?>, Set<?>> subsetOf = (a,b) -> b.containsAll(a);
  public static BiPredicate<Set<?>, Set<?>> supersetOf = (a,b) -> a.containsAll(b);
}
