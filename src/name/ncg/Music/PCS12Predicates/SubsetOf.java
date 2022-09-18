package name.ncg.Music.PCS12Predicates;

import com.google.common.base.Predicate;

import name.ncg.Music.PCS12;

public class SubsetOf implements Predicate<PCS12>  {

  private PCS12 pCS12;
  public SubsetOf(PCS12 pCS12) {
    this.pCS12 = pCS12;
  }
  @Override
  public boolean apply(PCS12 input) {
    return pCS12.intersect(input).getK() == input.getK();
  }

}
