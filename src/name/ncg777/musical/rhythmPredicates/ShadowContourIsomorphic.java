package name.ncg777.musical.rhythmPredicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.mathematics.objects.Sequence;
import name.ncg777.musical.Rhythm;

public class ShadowContourIsomorphic implements StandardAndGuavaPredicate<Rhythm> {

  @Override
  public boolean apply(@Nonnull Rhythm input) {
    if(input.getK()<2) return true;
    return Sequence.equivalentUnderRotation(input.getContour(), input.getShadowContour()) || 
        Sequence.equivalentUnderRotation(input.getContour().reverse(), input.getShadowContour());
  }
}
