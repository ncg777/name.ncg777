package name.NicolasCoutureGrenier.Maths.Relations;

import java.util.Set;
import java.util.function.BiPredicate;

public class SetRelations {
  public static <T> BiPredicate<Set<T>, Set<T>> subsetOf() { return (a,b) -> b.containsAll(a); }
  public static <T> BiPredicate<Set<T>, Set<T>> supersetOf() { return (a,b) -> a.containsAll(b); }
}
