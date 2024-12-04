package name.ncg777.music.pitchClassSet12Relations;

import java.util.BitSet;

import name.ncg777.maths.relations.Relation;
import name.ncg777.music.PitchClassSet12;


public class NNotesDifference implements Relation<PitchClassSet12, PitchClassSet12> {
  Integer m_n;

  public NNotesDifference(Integer p_n) {
    m_n = p_n;
  }

  @Override
  public boolean apply(PitchClassSet12 a, PitchClassSet12 b) {
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
