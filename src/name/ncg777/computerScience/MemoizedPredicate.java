package name.ncg777.computerScience;

import java.util.HashMap;
import java.util.function.Predicate;

public class MemoizedPredicate<T> implements Predicate<T> {
  private HashMap<T,Boolean> map = new HashMap<>();
  private Predicate<T> p;
  public MemoizedPredicate(Predicate<T> p) {
    this.p = p;
  }

  @Override
  public boolean test(T t) {
    if(map.containsKey(t)) return map.get(t);
    var v = p.test(t);
    map.put(t, v);
    return v;
  }}
