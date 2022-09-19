package name.ncg.Maths.Relations;

import name.ncg.Maths.DataStructures.Sequence;

public class SequenceRelations {
  public static Relation<Sequence,Sequence> equivalentUnderRotation = 
      Relation.fromBiPredicate((a,b) -> Sequence.equivalentUnderRotation(a, b));
}