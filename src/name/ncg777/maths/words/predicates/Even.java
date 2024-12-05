package name.ncg777.maths.words.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.words.BinaryWord;

public class Even implements StandardAndGuavaPredicate<BinaryWord> {

  @Override
  public boolean apply(@Nonnull BinaryWord arg0) {
    for(int n : arg0.getComposition().asSequence()) {if((n%2)==1) {return false;}}
    
    return true;
  }

}
