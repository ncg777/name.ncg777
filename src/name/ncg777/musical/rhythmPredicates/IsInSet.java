package name.ncg777.musical.rhythmPredicates;

import java.util.Set;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.musical.Rhythm;

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