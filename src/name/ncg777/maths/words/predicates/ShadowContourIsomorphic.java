package name.ncg777.maths.words.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.words.BinaryWord;

public class ShadowContourIsomorphic implements StandardAndGuavaPredicate<BinaryWord> {

  @Override
  public boolean apply(@Nonnull BinaryWord input) {
    if(input.getK()<2) return true;
    return Sequence.equivalentUnderRotation(input.getContour(), input.getShadowContour()) || 
        Sequence.equivalentUnderRotation(input.getContour().reverse(), input.getShadowContour());
  }
}
