package name.ncg.Music.PCS12Relations;

import java.util.BitSet;

import name.ncg.Maths.Relations.Relation;
import name.ncg.Music.PCS12;


public class NNotesDifference implements Relation<PCS12, PCS12> {
  Integer m_n;

  public NNotesDifference(Integer p_n) {
    m_n = p_n;
  }

  @Override
  public boolean apply(PCS12 a, PCS12 b) {
    if (a == null || b == null) {
      return false;
    }
    if (a.getK() != b.getK()) {
      return false;
    }
    BitSet x = new BitSet();
    x.or(a);
    x.and(b);
    return (b.getK() - x.cardinality()) == m_n;
  }

}
