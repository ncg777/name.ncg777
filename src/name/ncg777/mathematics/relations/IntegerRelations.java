package name.ncg777.mathematics.relations;

import name.ncg777.mathematics.Numbers;

public class IntegerRelations {
  
  public static Relation<Integer,Integer> divides = 
      Relation.fromBiPredicate((k,n) -> Numbers.divides(k, n));
}