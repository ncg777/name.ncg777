package name.ncg777.maths.words.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.words.BinaryWord;

public class ScaleModuloIsomorphic implements StandardAndGuavaPredicate<BinaryWord>  {

  private int n;
  private int k;
  public ScaleModuloIsomorphic(int k, int n){
    this.n = n;
    this.k = k;
  }
  @Override
  public boolean apply(@Nonnull BinaryWord input) {
    return input.equals(input.scaleModulo(k, n));
  }

}
