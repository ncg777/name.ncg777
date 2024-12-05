package name.ncg777.maths.music.pcs12.relations;

import java.util.BitSet;

import name.ncg777.maths.music.pcs12.Pcs12;
import name.ncg777.maths.relations.Relation;


public class NNotesDifference implements Relation<Pcs12, Pcs12> {
  Integer m_n;

  public NNotesDifference(Integer p_n) {
    m_n = p_n;
  }

  @Override
  public boolean apply(Pcs12 a, Pcs12 b) {
    if (a == null || b == null) {
      return false;
    }
    if (a.getK() != b.getK()) {
      return false;
    }
    BitSet x = new BitSet();
    x.or(a.getCombinationCopy());
    x.and(b.getCombinationCopy());
    return (b.getK() - x.cardinality()) == m_n;
  }

}
