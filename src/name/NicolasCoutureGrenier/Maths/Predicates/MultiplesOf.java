package name.NicolasCoutureGrenier.Maths.Predicates;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;

import java.util.Iterator;
import java.util.TreeSet;


public class MultiplesOf implements StandardAndGuavaPredicate<Integer> {
  TreeSet<Integer> m_t;
  boolean ones;

  public MultiplesOf(TreeSet<Integer> p_t, boolean p_ones) {
    m_t = new TreeSet<Integer>(p_t);
    ones = p_ones;
  }

  public MultiplesOf(Integer p_t, boolean p_ones) {
    ones = p_ones;
    m_t = new TreeSet<Integer>();
    m_t.add(p_t);

  }

  @SuppressWarnings("null")
  @Override
  public boolean apply(Integer k) {
    if (ones && k == 1) {
      return true;
    }

    boolean ok = false;
    Iterator<Integer> j = m_t.iterator();
    while (j.hasNext()) {
      if (k % j.next() == 0) {
        ok = true;
        break;
      }
    }

    return ok;
  }

}
