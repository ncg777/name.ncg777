package name.ncg.Music.PCS12Predicates;

import com.google.common.base.Predicate;
import java.util.Set;
import java.util.TreeSet;
import name.ncg.Music.PCS12;

public class OrderIn implements Predicate<PCS12> {
  Set<Integer> s;

  public OrderIn(Set<Integer> p_s) {
    s = p_s;
  }

  public OrderIn(Integer p_s) {

    s = new TreeSet<Integer>();
    s.add(p_s);
  }

  public boolean apply(PCS12 o) {
    return s.contains(o.getOrder());
  }
}
