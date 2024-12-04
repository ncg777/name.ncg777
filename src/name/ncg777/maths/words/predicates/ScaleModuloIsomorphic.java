package name.ncg777.maths.words.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.objects.WordBinary;

public class ScaleModuloIsomorphic implements StandardAndGuavaPredicate<WordBinary>  {

  private int n;
  private int k;
  public ScaleModuloIsomorphic(int k, int n){
    this.n = n;
    this.k = k;
  }
  @Override
  public boolean apply(@Nonnull WordBinary input) {
    return input.equals(input.scaleModulo(k, n));
  }

}
