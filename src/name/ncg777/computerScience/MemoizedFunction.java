package name.ncg777.computerScience;

import java.util.HashMap;
import java.util.function.Function;

public class MemoizedFunction<T,R> implements Function<T, R> {
  private HashMap<T,R> map = new HashMap<>();
  private Function<T,R> f;
  public MemoizedFunction(Function<T,R> f) {
    this.f = f;
  }

  @Override
  public R apply(T t) {
    if(map.containsKey(t)) return map.get(t);
    var v = f.apply(t);
    map.put(t, v);
    return v;
  }}
