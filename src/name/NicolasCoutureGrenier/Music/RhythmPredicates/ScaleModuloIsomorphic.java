package name.NicolasCoutureGrenier.Music.RhythmPredicates;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Music.Rhythm;

public class ScaleModuloIsomorphic implements StandardAndGuavaPredicate<Rhythm>  {

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
