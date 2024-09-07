package name.NicolasCoutureGrenier.Music.RhythmPredicates;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Maths.Numbers;
import name.NicolasCoutureGrenier.Maths.Objects.Sequence;
import name.NicolasCoutureGrenier.Music.Rhythm;


public class RelativelyFlat implements StandardAndGuavaPredicate<Rhythm> {
  HasNoGaps h = new HasNoGaps();

  @Override
  public boolean apply(Rhythm input) {
    if (!h.apply(input)) {
      return false;
    }

    Sequence a = input.getIntervalVector();
    a.removeIf((v) -> v == 0);
    
    int n = a.size();
    
    double mean = Numbers.triangularNumber(input.getK()) / (double) n;

    for (int i = 0; i < a.size(); i++) {
      if(Math.abs(((double) a.get(i)) - mean) > 0.5*mean) {return false;}
    }
    return true;
  }


}
