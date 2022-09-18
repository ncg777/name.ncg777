package name.ncg.Music.PCS12Relations;

import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Maths.Relations.Relation;
import name.ncg.Music.PCS12;

public class CloseIVs implements Relation<PCS12, PCS12> {

  @Override
  public boolean apply(PCS12 a, PCS12 b) {
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
