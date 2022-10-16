package name.ncg.Music.RhythmPredicates;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.Rhythm;


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

    for (int i = 0; i < a.size(); i++) {
      s += a.get(i);
    }
    double m = s / (double) a.size();
    s = 0;
    for (int i = 0; i < a.size(); i++) {
      s += Math.abs(((double) a.get(i)) - m);
    }
    double sd = s / (double) a.size();

    return (sd / m <= (((double) m_pc) / 100.0));

  }

  public RelativelyFlat(int pc) {
    m_pc = 100 - pc;
    h = new HasNoGaps();

  }

}
