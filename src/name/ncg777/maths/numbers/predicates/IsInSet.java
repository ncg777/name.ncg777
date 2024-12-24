package name.ncg777.maths.numbers.predicates;

import java.util.Set;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.numbers.BinaryNatural;

public class IsInSet implements StandardAndGuavaPredicate<BinaryNatural> {
  private Set<BinaryNatural> set;
  public IsInSet(Set<BinaryNatural> set) {
    this.set = set;
  }
  @Override
  public boolean apply(@Nonnull BinaryNatural r) {
    boolean output = set.contains(r);
    return output;
  }

}