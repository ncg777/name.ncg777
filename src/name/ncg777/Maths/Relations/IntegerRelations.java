package name.ncg777.Maths.Relations;

import name.ncg777.Maths.Numbers;

public class IntegerRelations {
  
  public static Relation<Integer,Integer> divides = 
      Relation.fromBiPredicate((k,n) -> Numbers.divides(k, n));
}