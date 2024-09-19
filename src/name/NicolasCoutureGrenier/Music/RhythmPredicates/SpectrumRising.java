package name.NicolasCoutureGrenier.Music.RhythmPredicates;

import javax.annotation.Nonnull;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Maths.Objects.Sequence;
import name.NicolasCoutureGrenier.Music.Rhythm;

public class SpectrumRising implements StandardAndGuavaPredicate<Rhythm>  {

    @Override
    public boolean apply(@Nonnull Rhythm arg0) {
      
      Sequence spectrum = Rhythm.calcSpectrum(arg0);
      spectrum.removeIf((v) -> v==0);
      if(spectrum.size() <= 1) return true;
      for(int i=1;i<spectrum.size();i++) { if(spectrum.get(i) < spectrum.get(i-1)) return false; }
      return true;
    }

}

