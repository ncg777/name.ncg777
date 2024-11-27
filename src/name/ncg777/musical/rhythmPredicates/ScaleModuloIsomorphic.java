package name.ncg777.musical.rhythmPredicates;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.musical.Rhythm;

public class ScaleModuloIsomorphic implements StandardAndGuavaPredicate<Rhythm>  {

  private int n;
  private int k;
  public ScaleModuloIsomorphic(int k, int n){
    this.n = n;
    this.k = k;
  }
  @Override
  public boolean apply(@Nonnull Rhythm input) {
    return input.equals(input.scaleModulo(k, n));
  }

}
