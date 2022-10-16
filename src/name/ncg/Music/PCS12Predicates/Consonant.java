package name.ncg.Music.PCS12Predicates;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.PCS12;

public class Consonant implements StandardAndGuavaPredicate<PCS12> {
  public boolean apply(PCS12 input) {
    Sequence IV = input.getIntervalVector();
    return IV.get(0) == 0;
  }
}
