package name.ncg777.maths.predicates.words;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.objects.Sequence;
import name.ncg777.maths.objects.words.BinaryWord;

public class ShadowContourIsomorphic implements StandardAndGuavaPredicate<BinaryWord> {

  @Override
  public boolean apply(@Nonnull BinaryWord input) {
    if(input.getK()<2) return true;
    return Sequence.equivalentUnderRotation(input.getContour(), input.getShadowContour()) || 
        Sequence.equivalentUnderRotation(input.getContour().reverse(), input.getShadowContour());
  }
}
