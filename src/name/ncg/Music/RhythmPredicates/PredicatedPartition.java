package name.ncg.Music.RhythmPredicates;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Music.Rhythm;
import name.ncg.Music.Rhythm16Partition;

import com.google.common.base.Predicate;

public class PredicatedPartition implements StandardAndGuavaPredicate<Rhythm16Partition>  {

  public PredicatedPartition(Predicate<Rhythm> pred){
    p = pred;
  }
  private Predicate<Rhythm> p;
  @Override
  public boolean apply(Rhythm16Partition arg0) {
    for(Rhythm r : arg0.getRhythms()){
      if(!p.apply(r)){
        return false;
      }
    }
    return true;
  }

}
