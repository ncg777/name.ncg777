package name.ncg777.maths.numbers.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.numbers.BinaryNumber;

public class Even implements StandardAndGuavaPredicate<BinaryNumber> {

  @Override
  public boolean apply(@Nonnull BinaryNumber arg0) {
    for(int n : arg0.getComposition().asSequence()) {if((n%2)==1) {return false;}}
    
    return true;
  }

}
