package name.ncg777.maths.numbers.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.sequences.Sequence;

public class ShadowContourIsomorphic implements StandardAndGuavaPredicate<BinaryNatural> {
  public ShadowContourIsomorphic() { }
  @Override
  public boolean apply(@Nonnull BinaryNatural input) {
    if(input.getK()<2) return true;
    var c = input.getContour();
    var s = input.getShadowContour();
    return Sequence.equivalentUnderRotation(c, s) || 
        Sequence.equivalentUnderRotation(
            c.reverse(), 
            s);
  }
}
