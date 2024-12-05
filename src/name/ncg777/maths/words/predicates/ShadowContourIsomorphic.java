package name.ncg777.maths.words.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.words.BinaryWord;

public class ShadowContourIsomorphic implements StandardAndGuavaPredicate<BinaryWord> {
  public ShadowContourIsomorphic() { }
  @Override
  public boolean apply(@Nonnull BinaryWord input) {
    if(input.getK()<2) return true;
    var c = input.getContour();
    var s = input.getShadowContour();
    return Sequence.equivalentUnderRotation(c, s) || 
        Sequence.equivalentUnderRotation(
            c.reverse(), 
            s);
  }
}
