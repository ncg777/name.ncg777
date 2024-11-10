package name.ncg777.Music.RhythmPredicates;

import javax.annotation.Nonnull;

import name.ncg777.CS.Functional.StandardAndGuavaPredicate;
import name.ncg777.Maths.Objects.Sequence;
import name.ncg777.Music.Rhythm;

public class Oddity implements StandardAndGuavaPredicate<Rhythm>  {

  @Override
  public boolean apply(@Nonnull Rhythm arg0) {
    int n = arg0.getN();
    if(n%2==1){return true;}
    
    Sequence spectrum = Rhythm.calcSpectrum(arg0);
    return spectrum.get(spectrum.size()-1) == 0;
  }

}
