package name.ncg.Music.RhythmPredicates;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.Rhythm;

public class Oddity implements StandardAndGuavaPredicate<Rhythm>  {

  @Override
  public boolean apply(Rhythm arg0) {
    int n = arg0.getN();
    if(n%2==1){return true;}
    
    Sequence spectrum = Rhythm.calcSpectrum(arg0);
    return spectrum.get(spectrum.size()-1) == 0;
  }

}
