package name.NicolasCoutureGrenier.Music.RhythmRelations;

import name.NicolasCoutureGrenier.Maths.DataStructures.Matrix;
import name.NicolasCoutureGrenier.Maths.Relations.Relation;
import name.NicolasCoutureGrenier.Music.R16List;
import name.NicolasCoutureGrenier.Music.Rhythm16;
import name.NicolasCoutureGrenier.Music.Rhythm16Partition;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.RelativelyFlat;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.ShadowContourIsomorphic;

public class Rhythm16PartitionFlatAndSci implements Relation<Rhythm16Partition, Rhythm16Partition>  {

  static private RelativelyFlat r = new RelativelyFlat(90);
  static private ShadowContourIsomorphic sci = new ShadowContourIsomorphic();
  @Override
  public boolean apply(Rhythm16Partition a, Rhythm16Partition b) {
    R16List l = new R16List();
    l.add(a.getRhythm()); l.add(b.getRhythm());
    if(!r.apply(l.asRhythm())){return false;}
    
    Rhythm16 zeroRhythm = Rhythm16.getZeroRhythm();
    int m = Math.max(a.getRhythms().size(), b.getRhythms().size());
    Matrix<Rhythm16> mat = new Matrix<Rhythm16>(m,2, zeroRhythm);
    int 
    k = 0; for(Rhythm16 r : a.getRhythms()){mat.set(k++,0,r);}
    k = 0; for(Rhythm16 r : b.getRhythms()){mat.set(k++,1,r);}
    
    for(int i=0;i<m;i++){
      R16List r = new R16List(mat.getRow(i));
      if(!sci.apply(r.asRhythm())){return false;}
    }
    
    return true;
  }

}
