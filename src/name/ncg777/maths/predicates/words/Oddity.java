package name.ncg777.maths.predicates.words;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.objects.Sequence;
import name.ncg777.maths.objects.words.BinaryWord;

public class Oddity implements StandardAndGuavaPredicate<BinaryWord>  {

  @Override
  public boolean apply(@Nonnull BinaryWord arg0) {
    int n = arg0.getN();
    if(n%2==1){return true;}
    
    Sequence spectrum = BinaryWord.calcSpectrum(arg0);
    return spectrum.get(spectrum.size()-1) == 0;
  }

}
