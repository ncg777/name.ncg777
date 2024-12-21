package name.ncg777.maths.numbers.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.numbers.BinaryNumber;
import name.ncg777.maths.sequences.Sequence;

public class Oddity implements StandardAndGuavaPredicate<BinaryNumber>  {

  @Override
  public boolean apply(@Nonnull BinaryNumber arg0) {
    int n = arg0.getN();
    if(n%2==1){return true;}
    
    Sequence spectrum = BinaryNumber.calcSpectrum(arg0);
    return spectrum.get(spectrum.size()-1) == 0;
  }

}
