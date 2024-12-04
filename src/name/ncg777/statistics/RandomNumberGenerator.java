package name.ncg777.statistics;

import java.util.Calendar;

import org.apache.commons.math3.random.AbstractRandomGenerator;
import org.apache.commons.math3.random.MersenneTwister;

public class RandomNumberGenerator {
  private static AbstractRandomGenerator rnd = new AbstractRandomGenerator() {
    private MersenneTwister rnd0 = new MersenneTwister(Calendar.getInstance().getTimeInMillis());

    @Override
    public void setSeed(long seed) {
      rnd0.setSeed(seed);
    }

    @Override
    public double nextDouble() {
      return rnd0.nextDouble();
    }
  };

  public static double nextDouble() {
    return rnd.nextDouble();
  }

  public static int nextInt() {
    return rnd.nextInt();
  }

  public static int nextInt(int n) {
    return rnd.nextInt(n);
  }

  public static double nextGaussian() {
    return rnd.nextGaussian();
  }
}
