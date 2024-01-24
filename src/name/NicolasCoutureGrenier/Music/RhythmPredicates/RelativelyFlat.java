package name.NicolasCoutureGrenier.Music.RhythmPredicates;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;
import name.NicolasCoutureGrenier.Music.Rhythm;


public class RelativelyFlat implements StandardAndGuavaPredicate<Rhythm> {
  int m_pc;
  HasNoGaps h;

  @Override
  public boolean apply(Rhythm input) {
    if (!h.apply(input)) {
      return false;
    }

    Sequence a = input.getIntervalVector();

    double s = 0;
    int n = a.size();
    
    for (int i = 0; i < a.size(); i++) {
      s += a.get(i);
      if(a.get(i) == 0) n--;
    }
    double m = s / (double) n;
    s = 0;
    for (int i = 0; i < a.size(); i++) {
      if(a.get(i)==0) continue;
      s += Math.abs(((double) a.get(i)) - m);
    }
    double sd = s / (double) n;
    // I can't remember the point of this formula...
    return (sd / m <= (((double) m_pc) / 100.0));

  }

  public RelativelyFlat(int pc) {
    m_pc = 100 - pc;
    h = new HasNoGaps();

  }

}
