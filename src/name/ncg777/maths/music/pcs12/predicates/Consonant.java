package name.ncg777.maths.music.pcs12.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.music.pcs12.Pcs12;
import name.ncg777.maths.sequences.Sequence;

public class Consonant implements StandardAndGuavaPredicate<Pcs12> {
  public boolean apply(@Nonnull Pcs12 input) {
    Sequence IV = input.getIntervalVector();
    return IV.get(0) == 0;
  }
}
