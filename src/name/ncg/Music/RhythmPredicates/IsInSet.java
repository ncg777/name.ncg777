package name.ncg.Music.RhythmPredicates;

import name.ncg.Music.Rhythm;

import java.util.Set;

import com.google.common.base.Predicate;

public class IsInSet implements Predicate<Rhythm> {
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