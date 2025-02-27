package name.ncg777.maths.numbers.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.predicates.OnCompositions;

public class DuplePartitioned implements StandardAndGuavaPredicate<BinaryNatural> {
  @Override
  public boolean apply(@Nonnull BinaryNatural input) {
    return OnCompositions.duplePartitioned.test(input.getComposition());
  }
}