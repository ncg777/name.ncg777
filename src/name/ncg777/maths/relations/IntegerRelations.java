package name.ncg777.maths.relations;

import name.ncg777.maths.Numbers;

public class IntegerRelations {
  
  public static Relation<Integer,Integer> divides = 
      Relation.fromBiPredicate((k,n) -> Numbers.divides(k, n));
}