package name.NicolasCoutureGrenier.Music.RhythmRelations;

import java.util.List;

import name.NicolasCoutureGrenier.Maths.Relations.Relation;
import name.NicolasCoutureGrenier.Music.Rhythm16;
import name.NicolasCoutureGrenier.Music.Rhythm16Partition;

public class Aligned implements Relation<Rhythm16Partition,Rhythm16Partition>{

  @Override
  public boolean apply(Rhythm16Partition a, Rhythm16Partition b) {
    List<Rhythm16> l1 = a.getRhythms();
    List<Rhythm16> l2 = b.getRhythms();
    int n = l1.size();
    if(n != l2.size()) {return false;}
   
    for(int i=0;i<n;i++) {
      if(l1.get(i).getPhase() != l2.get(i).getPhase()) {
        return false;
      }
      
    }
    return true;
  }
}
