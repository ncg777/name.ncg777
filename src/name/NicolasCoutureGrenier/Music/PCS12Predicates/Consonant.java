package name.NicolasCoutureGrenier.Music.PCS12Predicates;

import javax.annotation.Nonnull;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Maths.Objects.Sequence;
import name.NicolasCoutureGrenier.Music.PCS12;

public class Consonant implements StandardAndGuavaPredicate<PCS12> {
  public boolean apply(@Nonnull PCS12 input) {
    Sequence IV = input.getIntervalVector();
    return IV.get(0) == 0 && IV.get(5)==0;
  }
}
