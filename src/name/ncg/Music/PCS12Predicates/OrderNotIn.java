package name.ncg.Music.PCS12Predicates;

import java.util.Set;
import java.util.TreeSet;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Music.PCS12;

public class OrderNotIn implements StandardAndGuavaPredicate<PCS12> {
  Set<Integer> s;

  public OrderNotIn(Set<Integer> p_s) {
    s = p_s;
  }

  public OrderNotIn(Integer p_s) {

    s = new TreeSet<Integer>();
    s.add(p_s);
  }

  public boolean apply(PCS12 o) {
    return !s.contains(o.getOrder());
  }
}
