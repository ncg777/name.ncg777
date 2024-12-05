package name.ncg777.maths.music.pcs12.predicates;

import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.music.pcs12.Pcs12;

public class OrderIn implements StandardAndGuavaPredicate<Pcs12> {
  Set<Integer> s;

  public OrderIn(Set<Integer> p_s) {
    s = p_s;
  }

  public OrderIn(Integer p_s) {

    s = new TreeSet<Integer>();
    s.add(p_s);
  }

  public boolean apply(@Nonnull Pcs12 o) {
    return s.contains(o.getOrder());
  }
}
