package name.ncg777.maths.music.pcs12.predicates;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.music.pcs12.Pcs12;

public class SubsetOf implements StandardAndGuavaPredicate<Pcs12>  {

  private Pcs12 Pcs12;
  public SubsetOf(Pcs12 Pcs12) {
    this.Pcs12 = Pcs12;
  }
  @Override
  public boolean apply(@Nonnull Pcs12 input) {
    return Pcs12.intersect(input).getK() == input.getK();
  }

}
