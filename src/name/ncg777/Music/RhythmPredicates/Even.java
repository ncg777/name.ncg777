package name.ncg777.Music.RhythmPredicates;

import javax.annotation.Nonnull;

import name.ncg777.CS.Functional.StandardAndGuavaPredicate;
import name.ncg777.Music.Rhythm;

public class Even implements StandardAndGuavaPredicate<Rhythm> {

  @Override
  public boolean apply(@Nonnull Rhythm arg0) {
    for(int n : arg0.getComposition().asSequence()) {if((n%2)==1) {return false;}}
    
    return true;
  }

}
