package name.ncg777.maths.words.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.objects.Sequence;
import name.ncg777.maths.objects.WordBinary;

public class ShadowContourIsomorphic implements StandardAndGuavaPredicate<WordBinary> {

  @Override
  public boolean apply(@Nonnull WordBinary input) {
    if(input.getK()<2) return true;
    return Sequence.equivalentUnderRotation(input.getContour(), input.getShadowContour()) || 
        Sequence.equivalentUnderRotation(input.getContour().reverse(), input.getShadowContour());
  }
}
