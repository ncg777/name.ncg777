package name.ncg777.maths.music.pcs12.relations;

import java.util.BitSet;

import name.ncg777.maths.music.pcs12.Pcs12;
import name.ncg777.maths.relations.Relation;

public class CommonNotesAtLeast implements Relation<Pcs12, Pcs12>   {

  private int n = -1;
  
  public CommonNotesAtLeast(int n) {
    if(n < 1) throw new RuntimeException("CommonNotesAtLeast:: n >= 1");
    this.n = n;
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
    return x.cardinality() >= this.n;
  }

}
