package name.NicolasCoutureGrenier.Maths.Relations;

import name.NicolasCoutureGrenier.Maths.Numbers;

public class IntegerRelations {
  
  public static Relation<Integer,Integer> divides = 
      Relation.fromBiPredicate((k,n) -> Numbers.divides(k, n));
}