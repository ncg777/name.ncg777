package name.ncg777.maths.words.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.objects.Sequence;
import name.ncg777.maths.objects.WordBinary;

public class Oddity implements StandardAndGuavaPredicate<WordBinary>  {

  @Override
  public boolean apply(@Nonnull WordBinary arg0) {
    int n = arg0.getN();
    if(n%2==1){return true;}
    
    Sequence spectrum = WordBinary.calcSpectrum(arg0);
    return spectrum.get(spectrum.size()-1) == 0;
  }

}
