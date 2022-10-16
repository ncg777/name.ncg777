package name.ncg.Music.RhythmPredicates;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Music.Rhythm;

public class Even implements StandardAndGuavaPredicate<Rhythm> {

  @Override
  public boolean apply(Rhythm arg0) {
    for(int n : arg0.getComposition().asSequence()) {if((n%2)==1) {return false;}}
    
    return true;
  }

}
