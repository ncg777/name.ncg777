package name.ncg777.statistical;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
/**
 * The {@code SumOfDistanceToMean} class implements the {@code UnivariateStatistic} interface,
 * providing a method to calculate the sum of absolute distances of values from their mean.
 * 
 * <p>This statistic is useful in statistical analysis to measure the dispersion of data points
 * relative to the mean value. It essentially quantifies how far each value in a dataset is 
 * from the average, without considering the direction of the difference.</p>
 *
 * <p>Key methods include:</p>
 * <ul>
 * <li>{@code evaluate(double[] values)}: Computes the sum of distances of the given values 
 * from their mean.</li>
 * <li>{@code evaluate(double[] values, int begin, int length)}: Computes the sum of distances 
 * for a specified subarray of the given values, defined by the starting index and length.</li>
 * <li>{@code copy()}: Returns a copy of the current statistic instance. In this case, as there 
 * is no internal state, it simply returns the instance itself.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>
 * double[] data = {1.0, 2.0, 3.0, 4.0};
 * SumOfDistanceToMean statistic = new SumOfDistanceToMean();
 * double result = statistic.evaluate(data); // Computes the sum of distances of all values from the mean
 * </pre>
 * 
 * @see UnivariateStatistic
 * @see StatUtils
 */
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
