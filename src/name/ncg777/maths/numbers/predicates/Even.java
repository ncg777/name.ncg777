package name.ncg777.maths.numbers.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.numbers.BinaryNatural;

public class Even implements StandardAndGuavaPredicate<BinaryNatural> {

  @Override
  public boolean apply(@Nonnull BinaryNatural input) {
    for(int n : input.getComposition().asSequence()) {if((n%2)==1) {return false;}}
    
    return true;
  }

}
