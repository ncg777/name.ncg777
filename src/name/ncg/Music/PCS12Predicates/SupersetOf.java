package name.ncg.Music.PCS12Predicates;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Music.PCS12;

public class SupersetOf implements StandardAndGuavaPredicate<PCS12>  {

  private PCS12 pCS12;
  public SupersetOf(PCS12 pCS12) {
    this.pCS12 = pCS12;
  }
  @Override
  public boolean apply(PCS12 input) {
    return pCS12.combineWith(input).getK() == input.getK();
  }

}
