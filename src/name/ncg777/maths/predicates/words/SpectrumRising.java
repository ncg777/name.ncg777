package name.ncg777.maths.predicates.words;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.objects.Sequence;
import name.ncg777.maths.objects.words.BinaryWord;

public class SpectrumRising implements StandardAndGuavaPredicate<BinaryWord>  {

    @Override
    public boolean apply(@Nonnull BinaryWord arg0) {
      
      Sequence spectrum = BinaryWord.calcSpectrum(arg0);
      spectrum.removeIf((v) -> v==0);
      if(spectrum.size() <= 1) return true;
      for(int i=1;i<spectrum.size();i++) { if(spectrum.get(i) < spectrum.get(i-1)) return false; }
      return true;
    }

}

