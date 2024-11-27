package name.ncg777.musical.pitchClassSet12Relations;

import java.util.BitSet;

import name.ncg777.mathematics.relations.Relation;
import name.ncg777.musical.pitchClassSet12;


public class NNotesDifference implements Relation<pitchClassSet12, pitchClassSet12> {
  Integer m_n;

  public NNotesDifference(Integer p_n) {
    m_n = p_n;
  }

  @Override
  public boolean apply(pitchClassSet12 a, pitchClassSet12 b) {
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
