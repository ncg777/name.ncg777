package name.ncg777.maths.numbers.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.numbers.BinaryNumber;
import name.ncg777.maths.sequences.Sequence;

public class SpectrumRising implements StandardAndGuavaPredicate<BinaryNumber>  {

    @Override
    public boolean apply(@Nonnull BinaryNumber arg0) {
      
      Sequence spectrum = BinaryNumber.calcSpectrum(arg0);
      spectrum.removeIf((v) -> v==0);
      if(spectrum.size() <= 1) return true;
      for(int i=1;i<spectrum.size();i++) { if(spectrum.get(i) < spectrum.get(i-1)) return false; }
      return true;
    }

}

