package name.ncg777.maths.numbers.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.numbers.BinaryNumber;
import name.ncg777.maths.sequences.Sequence;

public class ShadowContourIsomorphic implements StandardAndGuavaPredicate<BinaryNumber> {
  public ShadowContourIsomorphic() { }
  @Override
  public boolean apply(@Nonnull BinaryNumber input) {
    if(input.getK()<2) return true;
    var c = input.getContour();
    var s = input.getShadowContour();
    return Sequence.equivalentUnderRotation(c, s) || 
        Sequence.equivalentUnderRotation(
            c.reverse(), 
            s);
  }
}
