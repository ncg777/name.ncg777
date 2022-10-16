package name.NicolasCoutureGrenier.Music.RhythmPredicates;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Maths.FuzzyLogic.Valuators.CombinationDispersion;
import name.NicolasCoutureGrenier.Music.Rhythm;

public class EntropicDispersion implements StandardAndGuavaPredicate<Rhythm> {
  private CombinationDispersion cd = new CombinationDispersion();
  
  @Override
  public boolean apply(Rhythm arg0) {
    return cd.apply(arg0).isEntropic(0.025);
  }

}
