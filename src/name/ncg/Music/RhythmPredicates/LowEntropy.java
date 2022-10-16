package name.ncg.Music.RhythmPredicates;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Maths.FuzzyLogic.Valuators.CombinationEntropy;
import name.ncg.Music.Rhythm;

public class LowEntropy implements StandardAndGuavaPredicate<Rhythm> {
  private CombinationEntropy cd = new CombinationEntropy();
  
  @Override
  public boolean apply(Rhythm arg0) {
    return cd.apply(arg0).not().quantize(0.75);
  }

}
