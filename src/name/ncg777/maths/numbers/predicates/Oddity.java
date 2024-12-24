package name.ncg777.maths.numbers.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.sequences.Sequence;

public class Oddity implements StandardAndGuavaPredicate<BinaryNatural>  {

  @Override
  public boolean apply(@Nonnull BinaryNatural arg0) {
    int n = arg0.getN();
    if(n%2==1){return true;}
    
    Sequence spectrum = BinaryNatural.calcSpectrum(arg0);
    return spectrum.get(spectrum.size()-1) == 0;
  }

}
