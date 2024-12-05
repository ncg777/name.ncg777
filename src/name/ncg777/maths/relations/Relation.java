package name.ncg777.maths.relations;

import java.util.function.BiPredicate;

import com.google.common.base.Predicate;

import name.ncg777.computing.dataStructures.HeteroPair;

public interface Relation<T extends Comparable<? super T>, U extends Comparable<? super U>> extends BiPredicate<T,U>{
  public static <T extends Comparable<? super T>, U extends Comparable<? super U>> Relation<T,U> fromBiPredicate(BiPredicate<T,U> p) {
    return new Relation<T,U>() {
      public boolean apply(T a, U b) {
        return p.test(a, b);
      }
    };
  }
  public default boolean apply(HeteroPair<T,U> p) { return apply(p.getFirst(), p.getSecond()); }
  boolean apply(T a, U b);

  public default boolean test(T t, U u) {return apply(t,u); }
  
  public static <T, U> Predicate<T> bindSecond(
      U u, BiPredicate<T, U> r) {
    return (T t) -> r.test(t, u);
  }
  
   public static <T, U> Predicate<U> bindFirst(
       T t, BiPredicate<T, U> r) {
     return (U u) -> r.test(t, u);
   }
   
   @SafeVarargs
   public static <T extends Comparable<? super T>, U extends Comparable<? super U>> Relation<T, U> and(
       Relation<T, U>... rels) {
     return new Relation<T,U>() {
       public boolean apply(T a, U b) {
         for(var rel : rels) {
           if(!rel.apply(a, b)) {return false;}
         }
         return true;
       }
     };
   }
   @SafeVarargs
   public static <T extends Comparable<? super T>, U extends Comparable<? super U>> Relation<T, U> or(Relation<T, U>... rels) {
     return new Relation<T,U>() {
       public boolean apply(T a, U b) {
         for(var rel : rels) {
           if(rel.apply(a, b)) {return true;}
         }
         return false;
       }
     };
   }
  
   public static <T extends Comparable<? super T>, U extends Comparable<? super U>> Relation<T, U> not(
       Relation<T, U> r) {
     return new Relation<T,U>() {
       public boolean apply(T a, U b) {
         return !r.apply(a, b);
       }
     };
   }
}
