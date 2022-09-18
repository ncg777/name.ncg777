package name.ncg.Music.PCS12Predicates;

import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.PCS12;

import com.google.common.base.Predicate;

public class Consonant implements Predicate<PCS12> {
  public boolean apply(PCS12 input) {
    Sequence IV = input.getIntervalVector();
    return IV.get(0) == 0;
  }
}
