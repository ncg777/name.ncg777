package name.NicolasCoutureGrenier.Music.RhythmRelations;

import name.NicolasCoutureGrenier.Maths.Relations.Relation;
import name.NicolasCoutureGrenier.Music.MeasureRhythm;
import name.NicolasCoutureGrenier.Music.Rhythm;

public class CompatibleBeatRhythms implements Relation<Rhythm, Rhythm>{

  public CompatibleBeatRhythms() {
  }

  @Override
  public boolean apply(Rhythm a, Rhythm b) {
    MeasureRhythm ra = MeasureRhythm.fromRhythm(a);
    MeasureRhythm rb = MeasureRhythm.fromRhythm(b);
    
    if(ra == null || rb == null || ra.size() != rb.size()) throw new IllegalArgumentException();
    
    int n = ra.size();
    for(int i=0;i<n;i++) {
      int nx = ra.get(i).getN();
      int ny = rb.get(i).getN();
      if(nx > ny) {
        int tmp = ny;
        ny = nx;
        nx = tmp;
      }
      if(nx % ny != 0) return false;
    }
    return true;
  }

}
