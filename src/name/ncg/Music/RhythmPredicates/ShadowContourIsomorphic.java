package name.ncg.Music.RhythmPredicates;

import com.google.common.base.Predicate;

import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.Rhythm;

public class ShadowContourIsomorphic implements Predicate<Rhythm> {

  @Override
  public boolean apply(Rhythm input) {
    if(input.getK()<2) return true;
    return Sequence.equivalentUnderRotation(input.getContour(), input.getShadowContour());
  }
}
