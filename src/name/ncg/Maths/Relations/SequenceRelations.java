package name.ncg.Maths.Relations;

import name.ncg.Maths.DataStructures.Sequence;

public class SequenceRelations {
  public static Relation<Sequence,Sequence> equivalentUnderRotation = 
      Relation.fromBiPredicate((a,b) -> Sequence.equivalentUnderRotation(a, b));
  public static Relation<Sequence,Sequence> equivalentUnderRotationOrSymmetry = 
      Relation.fromBiPredicate((a,b) -> Sequence.equivalentUnderRotation(a, b) 
        || Sequence.equivalentUnderRotation(a, b.reverse())); 
}