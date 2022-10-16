package name.NicolasCoutureGrenier.Music.PCS12Relations;

import java.util.BitSet;

import name.NicolasCoutureGrenier.Maths.Relations.Relation;
import name.NicolasCoutureGrenier.Music.PCS12;

public class CommonNotesAtLeast implements Relation<PCS12, PCS12>   {

  private int n = -1;
  
  public CommonNotesAtLeast(int n) {
    if(n < 1) throw new RuntimeException("CommonNotesAtLeast:: n >= 1");
    this.n = n;
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
    return x.cardinality() >= this.n;
  }

}
