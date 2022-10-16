package name.ncg.Music.RhythmPredicates;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Music.Rhythm16Partition;

public class NoRepeat implements StandardAndGuavaPredicate<Rhythm16Partition> {

  @Override
  public boolean apply(Rhythm16Partition arg0) {
    Integer[] p = arg0.getPartition();
    for(int i=0;i<p.length;i++) {
      if(p[(i+1)%p.length] == p[i]){return false;}
    }
    return true;
  }

}
