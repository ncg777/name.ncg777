package name.NicolasCoutureGrenier.Maths.FuzzyLogic.Valuators;

import java.util.Iterator;
import java.util.Set;
import com.google.common.base.Function;

import name.NicolasCoutureGrenier.Maths.FuzzyLogic.FuzzyVariable;
import name.NicolasCoutureGrenier.Maths.Objects.Composition;
import name.NicolasCoutureGrenier.Maths.Objects.Sequence;

public class SequencePeriodicity implements Function<Sequence, FuzzyVariable> {
  CompositionDispersion ch;

  public SequencePeriodicity() {
    super();
    ch = new CompositionDispersion();
  }

  /**
   * Computes the mean dispersion of all compositions induced by occurrences of distinct values in
   * sequence s.
   * 
   * @param the sequence
   * @return mean of the dispersion for all distinct values of the sequence
   */
  @Override
  public FuzzyVariable apply(Sequence input) {
    Set<Integer> d = input.distinct();

    Double v = 0.0;

    Iterator<Integer> i = d.iterator();

    while (i.hasNext()) {
      v += ch.apply(composition(input, i.next())).getValue();
    }

    return FuzzyVariable.from(v / (double) d.size());
  }

  /**
   * Construct the composition induced by occurrences of v in sequence s.
   * 
   * @param s
   * @param v
   * @return
   */
  private Composition composition(Sequence s, Integer v) {
    Boolean[] b = new Boolean[s.size()];
    for (int i = 0; i < s.size(); i++) {
      b[i] = s.get(i).equals(v);
    }
    int f = 0;
    for (int i = 0; i < b.length; i++) {
      if (b[i]) {
        f = i;
        break;
      }
    }
    Boolean[] c = new Boolean[s.size() - 1];
    for (int i = 0; i < s.size() - 1; i++) {
      c[i] = b[(f + i + 1) % b.length];
    }

    return new Composition(c);

  }
}
