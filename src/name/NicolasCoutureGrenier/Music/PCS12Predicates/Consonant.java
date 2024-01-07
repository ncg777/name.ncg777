package name.NicolasCoutureGrenier.Music.PCS12Predicates;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;
import name.NicolasCoutureGrenier.Music.PCS12;

public class Consonant implements StandardAndGuavaPredicate<PCS12> {
  public boolean apply(PCS12 input) {
    Sequence IV = input.getIntervalVector();
    return IV.get(0) == 0 && IV.get(5)==0;
  }
}
