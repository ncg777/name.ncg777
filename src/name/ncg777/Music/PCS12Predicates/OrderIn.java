package name.ncg777.Music.PCS12Predicates;

import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;

import name.ncg777.CS.Functional.StandardAndGuavaPredicate;
import name.ncg777.Music.PCS12;

public class OrderIn implements StandardAndGuavaPredicate<PCS12> {
  Set<Integer> s;

  public OrderIn(Set<Integer> p_s) {
    s = p_s;
  }

  public OrderIn(Integer p_s) {

    s = new TreeSet<Integer>();
    s.add(p_s);
  }

  public boolean apply(@Nonnull PCS12 o) {
    return s.contains(o.getOrder());
  }
}
