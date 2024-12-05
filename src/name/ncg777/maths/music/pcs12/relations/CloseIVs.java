package name.ncg777.maths.music.pcs12.relations;

import name.ncg777.maths.music.pcs12.Pcs12;
import name.ncg777.maths.relations.Relation;
import name.ncg777.maths.sequences.Sequence;

public class CloseIVs implements Relation<Pcs12, Pcs12> {

  @Override
  public boolean apply(Pcs12 a, Pcs12 b) {
    Sequence iva = a.getIntervalVector();
    Sequence ivb = b.getIntervalVector();
    
    int acc = 0;
    
    for(int i=0;i<iva.size();i++) {
      if(iva.get(i) != ivb.get(i)) acc++;
      if(acc > 2) return false;
    }
    return true;
  }

}
