package name.ncg.Maths.Relations;

import name.ncg.Maths.Numbers;

public class IntegerRelations {
  
  public static Relation<Integer,Integer> divides = 
      Relation.fromBiPredicate((k,n) -> Numbers.divides(k, n));
}