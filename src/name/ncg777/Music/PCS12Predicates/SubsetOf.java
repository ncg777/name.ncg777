package name.ncg777.Music.PCS12Predicates;

import javax.annotation.Nonnull;

import name.ncg777.CS.Functional.StandardAndGuavaPredicate;
import name.ncg777.Music.PCS12;

public class SubsetOf implements StandardAndGuavaPredicate<PCS12>  {

  private PCS12 pCS12;
  public SubsetOf(PCS12 pCS12) {
    this.pCS12 = pCS12;
  }
  @Override
  public boolean apply(@Nonnull PCS12 input) {
    return pCS12.intersect(input).getK() == input.getK();
  }

}
