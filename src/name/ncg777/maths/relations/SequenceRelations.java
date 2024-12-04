package name.ncg777.maths.relations;

import name.ncg777.maths.objects.Sequence;

public class SequenceRelations {
  public static Relation<Sequence,Sequence> equivalentUnderRotation = 
      Relation.fromBiPredicate((a,b) -> Sequence.equivalentUnderRotation(a, b));
  public static Relation<Sequence,Sequence> equivalentUnderRotationOrSymmetry = 
      Relation.fromBiPredicate((a,b) -> Sequence.equivalentUnderRotation(a, b) 
        || Sequence.equivalentUnderRotation(a, b.reverse())); 
}