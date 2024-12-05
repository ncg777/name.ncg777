package name.ncg777.computing;

import java.util.HashMap;
import java.util.function.BiPredicate;

import org.apache.commons.math3.util.Pair;

public class MemoizedBiPredicate<T,U> implements BiPredicate<T,U> {
  private HashMap<Pair<T,U>,Boolean> map = new HashMap<>();
  private BiPredicate<T,U> p;
  public MemoizedBiPredicate(BiPredicate<T,U> p) {
    this.p = p;
  }

  @Override
  public boolean test(T t, U u) {
    Pair<T,U> pair = new Pair<>(t,u);
    if(map.containsKey(pair)) return map.get(pair);
    var v = p.test(t, u);
    map.put(pair, v);
    return v;
  }}
