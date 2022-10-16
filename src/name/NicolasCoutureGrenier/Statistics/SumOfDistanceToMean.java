package name.NicolasCoutureGrenier.Statistics;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;

public class SumOfDistanceToMean implements UnivariateStatistic {

  @Override
  public double evaluate(double[] values) {
    double m = StatUtils.mean(values);
    double acc = 0.0;
    for (int i = 0; i < values.length; i++) {
      acc += Math.abs(m - values[i]);
    }
    return acc;
  }

  @Override
  public double evaluate(double[] values, int begin, int length) {
    double m = StatUtils.mean(values, begin, length);
    double acc = 0.0;
    for (int i = 0; i < length; i++) {
      acc += Math.abs(m - values[begin + i]);
    }
    return acc;
  }

  @Override
  public UnivariateStatistic copy() {
    // there is no internal state...
    return this;
  }

}
