package name.ncg777.maths.numbers.predicates;

import java.util.Set;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.numbers.BinaryNumber;

public class IsInSet implements StandardAndGuavaPredicate<BinaryNumber> {
  private Set<BinaryNumber> set;
  public IsInSet(Set<BinaryNumber> set) {
    this.set = set;
  }
  @Override
  public boolean apply(@Nonnull BinaryNumber r) {
    boolean output = set.contains(r);
    return output;
  }

}