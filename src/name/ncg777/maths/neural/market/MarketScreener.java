package name.ncg777.maths.neural.market;

import java.time.*;
import java.util.*;

/** Converts ternary scenarios into empirical, training-only estimates of positive total return. */
public final class MarketScreener {
  private MarketScreener() {}
  public record Score(double oneDay, double twoDays, double testBrier, double baselineBrier, int testWindows) {}
  public static Score score(MarketForecaster.Result fit) {
    var bars = fit.trainingBars(); double[] returns = new double[bars.size() - 1];
    for (int i = 0; i < returns.length; i++) returns[i] = Math.log(bars.get(i + 1).close() / bars.get(i).close());
    int trainEnd = (int)(returns.length * .70), testStart = (int)(returns.length * .85);
    double[][] wins = new double[2][9]; int[] counts = new int[9]; double[] base = new double[2];
    int n = trainEnd - 7;
    for (int i = 6; i < trainEnd - 1; i++) {
      int code = code(returns[i], returns[i + 1], fit.threshold()); counts[code]++;
      if (returns[i] > 0) { wins[0][code]++; base[0]++; }
      if (returns[i] + returns[i + 1] > 0) { wins[1][code]++; base[1]++; }
    }
    for (int h = 0; h < 2; h++) {
      base[h] = (base[h] + .5) / (n + 1);
      for (int c = 0; c < 9; c++) wins[h][c] = (wins[h][c] + 5 * base[h]) / (counts[c] + 5);
    }
    double brier = 0, baseline = 0; int tests = 0;
    for (int i = testStart; i + 7 < returns.length; i++) {
      int[] context = new int[6];
      for (int j = 0; j < 6; j++) context[j] = MarketForecaster.digit(returns[i + j], fit.threshold());
      double p = dot(MarketForecaster.conditional(fit.model(), context), wins[1]);
      double actual = returns[i + 6] + returns[i + 7] > 0 ? 1 : 0;
      brier += (p - actual) * (p - actual); baseline += (base[1] - actual) * (base[1] - actual); tests++;
    }
    return new Score(dot(fit.joint(), wins[0]), dot(fit.joint(), wins[1]), brier / tests, baseline / tests, tests);
  }
  private static int code(double a, double b, double threshold) {
    return (MarketForecaster.digit(a, threshold) + 1) * 3 + MarketForecaster.digit(b, threshold) + 1;
  }
  private static double dot(double[] a, double[] b) { double sum = 0; for (int i = 0; i < a.length; i++) sum += a[i] * b[i]; return sum; }
  public static void requireFresh(MarketData.Snapshot snapshot, Instant now) {
    var bars = snapshot.completed();
    if (bars.isEmpty() || bars.get(bars.size() - 1).time().isBefore(now.minus(Duration.ofDays(7))))
      throw new IllegalArgumentException("No completed price within the last 7 calendar days");
  }
}
