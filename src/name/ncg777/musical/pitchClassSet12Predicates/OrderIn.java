package name.ncg777.musical.pitchClassSet12Predicates;

import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.musical.pitchClassSet12;

public class OrderIn implements StandardAndGuavaPredicate<pitchClassSet12> {
  Set<Integer> s;

  public OrderIn(Set<Integer> p_s) {
    s = p_s;
  }

  public OrderIn(Integer p_s) {

    s = new TreeSet<Integer>();
    s.add(p_s);
  }

  public boolean apply(@Nonnull pitchClassSet12 o) {
    return s.contains(o.getOrder());
  }
}
