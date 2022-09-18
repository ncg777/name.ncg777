package name.ncg.Music.RhythmPredicates;

import com.google.common.base.Predicate;

import name.ncg.Music.Rhythm;

public class ScaleModuloIsomorphic implements Predicate<Rhythm>  {

  private int n;
  private int k;
  public ScaleModuloIsomorphic(int k, int n){
    this.n = n;
    this.k = k;
  }
  @Override
  public boolean apply(Rhythm input) {
    return input.equals(input.scaleModulo(k, n));
  }

}
