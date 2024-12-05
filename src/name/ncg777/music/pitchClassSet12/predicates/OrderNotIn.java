package name.ncg777.music.pitchClassSet12.predicates;

import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.music.pitchClassSet12.PitchClassSet12;

public class OrderNotIn implements StandardAndGuavaPredicate<PitchClassSet12> {
  Set<Integer> s;

  public OrderNotIn(Set<Integer> p_s) {
    s = p_s;
  }

  public OrderNotIn(Integer p_s) {

    s = new TreeSet<Integer>();
    s.add(p_s);
  }

  public boolean apply(@Nonnull PitchClassSet12 o) {
    return !s.contains(o.getOrder());
  }
}
