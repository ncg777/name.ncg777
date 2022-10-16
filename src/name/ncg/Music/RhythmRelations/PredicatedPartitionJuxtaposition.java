package name.ncg.Music.RhythmRelations;

import java.util.function.Predicate;

import name.ncg.Maths.DataStructures.Matrix;
import name.ncg.Maths.Relations.Relation;
import name.ncg.Music.R16List;
import name.ncg.Music.Rhythm;
import name.ncg.Music.Rhythm16;
import name.ncg.Music.Rhythm16Partition;

public class PredicatedPartitionJuxtaposition implements 
  Relation<Rhythm16Partition, Rhythm16Partition>   {

  public PredicatedPartitionJuxtaposition(Predicate<Rhythm> pred){
    ld = pred;
  }
  
  private Predicate<Rhythm> ld;
  
  @Override
  public boolean apply(Rhythm16Partition a, Rhythm16Partition b) {
    R16List l = new R16List();
    l.add(a.getRhythm()); l.add(b.getRhythm());

    Rhythm16 zeroRhythm = Rhythm16.getZeroRhythm();
    int m = Math.max(a.getRhythms().size(), b.getRhythms().size());
    Matrix<Rhythm16> mat = new Matrix<Rhythm16>(m,2, zeroRhythm);
    int 
    k = 0; for(Rhythm16 r : a.getRhythms()){mat.set(k++,0,r);}
    k = 0; for(Rhythm16 r : b.getRhythms()){mat.set(k++,1,r);}
    
    for(int i=0;i<m;i++){
      R16List r = new R16List(mat.getRow(i));
      if(!ld.test(r.asRhythm())){return false;}
    }
    return true;
  }

}

