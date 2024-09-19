package name.NicolasCoutureGrenier.Music.RhythmPredicates;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Music.Rhythm;

import java.util.Set;

import javax.annotation.Nonnull;

public class IsInSet implements StandardAndGuavaPredicate<Rhythm> {
  private Set<Rhythm> set;
  public IsInSet(Set<Rhythm> set) {
    this.set = set;
  }
  @Override
  public boolean apply(@Nonnull Rhythm r) {
    boolean output = set.contains(r);
    return output;
  }

}