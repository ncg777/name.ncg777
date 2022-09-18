package name.ncg.Music.RhythmPredicates;

import name.ncg.Music.Rhythm;


import com.google.common.base.Predicate;

public class Even implements Predicate<Rhythm> {

  @Override
  public boolean apply(Rhythm arg0) {
    for(int n : arg0.getComposition().asSequence()) {if((n%2)==1) {return false;}}
    
    return true;
  }

}
