package name.ncg.CS;

import java.util.Collections;
import java.util.Enumeration;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Functional {
  public static <T,U,R> Function<T,R> bindSecond(
    BiFunction<T,U,R> b, final U u){
    return new Function<T,R>(){
      @Override public R apply(T t) {return b.apply(t, u);}};
  }
  
  public static <T,U,R> Function<U,R> bindFirst(
    BiFunction<T,U,R> b, final T t){
    return new Function<U,R>(){
      @Override public R apply(U u) {return b.apply(t, u);}};
  }
  
  public static <T,U> Predicate<T> bindSecond(BiPredicate<T,U> b, final U u){
     return new Predicate<T>(){
       @Override public boolean test(T t) {return b.test(t, u);}};
   }
                                               
  public static <T,U> Predicate<U> bindFirst(BiPredicate<T,U> b, final T t){
   return new Predicate<U>(){
     @Override public boolean test(U u) {return b.test(t, u);}};
  }
  
  public static <T> java.util.function.Predicate<T> convertFromGuava(com.google.common.base.Predicate<T> p) {
    return (t) -> p.apply(t);
  }
  
  public static <T> Stream<T> enumerationStream(Enumeration<T> e) {
    return Collections.list(e).stream();
  }
}
