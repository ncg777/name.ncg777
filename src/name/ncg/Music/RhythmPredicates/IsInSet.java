package name.ncg.Music.RhythmPredicates;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Music.Rhythm;

import java.util.Set;

public class IsInSet implements StandardAndGuavaPredicate<Rhythm> {
  private Set<Rhythm> set;
  public IsInSet(Set<Rhythm> set) {
    this.set = set;
  }
  @Override
  public boolean apply(Rhythm r) {
    boolean output = set.contains(r);
    return output;
  }

}