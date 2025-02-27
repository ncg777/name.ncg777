package name.ncg777.maths.numbers.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.Numbers;
import name.ncg777.maths.numbers.BinaryNatural;

public class DuplePartitioned implements StandardAndGuavaPredicate<BinaryNatural> {

  @Override
  public boolean apply(@Nonnull BinaryNatural input) {
    var c = input.getComposition().asSequence();
    int acc = c.get(0);
    for(int i=1;i<c.size();i++) {
      if(!Numbers.isPowerOfTwo(acc)) {
        if(c.get(i) > c.get(i-1)) return false;
      }
      acc += c.get(i);
    }
    return true;
  }
}
