package name.ncg777.maths.fuzzy.valuationFunctions;

import com.google.common.base.Function;

import name.ncg777.maths.fuzzy.FuzzyVariable;
import name.ncg777.maths.sequences.Sequence;

public class SequenceSymmetry implements Function<Sequence, FuzzyVariable> {

  @Override
  public FuzzyVariable apply(Sequence s) {
    int n = s.size();
    if (n < 4) {
      return FuzzyVariable.from(0.5);
    }

    boolean odd = (n % 2 == 1);

    int radius = (n - (odd ? 1 : 0)) / 2;

    Integer max = Integer.MIN_VALUE;

    for (int i = 0; i < n; i++) {
      Sequence before = new Sequence();
      Sequence after = new Sequence();
      for (int j = 0; j < radius; j++) {
        before.add(s.get((n + i - (j + (odd ? 1 : 0))) % n));
        after.add(s.get((i + (j + 1)) % n));
      }
      before = before.difference().antidifference(0);
      after = after.difference().antidifference(0);
      int t = 0;
      for (int j = 1; j < radius; j++) {
        if (before.get(j).equals(after.get(j))) {
          t++;
        }
      }

      if (t > max) {
        max = t;
      }
    }

    return FuzzyVariable.from((double) max / (double) (radius - 1));
  }

}
