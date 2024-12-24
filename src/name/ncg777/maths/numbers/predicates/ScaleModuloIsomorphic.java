package name.ncg777.maths.numbers.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.numbers.BinaryNatural;

public class ScaleModuloIsomorphic implements StandardAndGuavaPredicate<BinaryNatural>  {

  private int n;
  private int k;
  public ScaleModuloIsomorphic(int k, int n){
    this.n = n;
    this.k = k;
  }
  @Override
  public boolean apply(@Nonnull BinaryNatural input) {
    return input.equals(input.scaleModulo(k, n));
  }

}
