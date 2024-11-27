package name.ncg777.musical.pitchClassSet12Relations;

import name.ncg777.mathematics.objects.Sequence;
import name.ncg777.mathematics.relations.Relation;
import name.ncg777.musical.pitchClassSet12;

public class CloseIVs implements Relation<pitchClassSet12, pitchClassSet12> {

  @Override
  public boolean apply(pitchClassSet12 a, pitchClassSet12 b) {
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
