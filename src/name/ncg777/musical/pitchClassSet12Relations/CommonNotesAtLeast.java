package name.ncg777.musical.pitchClassSet12Relations;

import java.util.BitSet;

import name.ncg777.mathematics.relations.Relation;
import name.ncg777.musical.PitchClassSet12;

public class CommonNotesAtLeast implements Relation<PitchClassSet12, PitchClassSet12>   {

  private int n = -1;
  
  public CommonNotesAtLeast(int n) {
    if(n < 1) throw new RuntimeException("CommonNotesAtLeast:: n >= 1");
    this.n = n;
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
    return x.cardinality() >= this.n;
  }

}
