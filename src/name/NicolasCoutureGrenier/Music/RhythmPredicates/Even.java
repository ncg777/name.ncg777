package name.NicolasCoutureGrenier.Music.RhythmPredicates;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Music.Rhythm;

public class Even implements StandardAndGuavaPredicate<Rhythm> {

  @Override
  public boolean apply(Rhythm arg0) {
    for(int n : arg0.getComposition().asSequence()) {if((n%2)==1) {return false;}}
    
    return true;
  }

}
