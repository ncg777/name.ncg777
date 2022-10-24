package name.NicolasCoutureGrenier.Music.RhythmPredicates;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Maths.FuzzyLogic.Valuators.CombinationEntropy;
import name.NicolasCoutureGrenier.Music.Rhythm;

public class LowEntropy implements StandardAndGuavaPredicate<Rhythm> {
  private CombinationEntropy cd = new CombinationEntropy();
  
  @Override
  public boolean apply(Rhythm arg0) {
    return cd.apply(arg0).not().quantize(Math.exp(0.5)*Math.exp(-1.0));
  }

}
