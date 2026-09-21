package name.ncg777.maths.neural.market;

import java.util.*;
import java.util.concurrent.CancellationException;
import name.ncg777.maths.neural.*;
import name.ncg777.maths.physics.TernarySpinModel;

/** Eight-node, six-past/two-future sliding-window forecast with chronological evaluation. */
public final class MarketForecaster {
  public static final int WIDTH = 8, CONTEXT = 6;
  private MarketForecaster() {}
  public record Result(TernarySpinModel model, double threshold, double[] representativeReturns,
      double[] joint, double modelTestLoss, double baselineTestLoss, double modelValidationLoss,
      double baselineValidationLoss, boolean useNetwork, int trainRows, int validationRows, int testRows,
      int selectedEpoch, long work, List<MarketData.Bar> trainingBars) {
    public Result {
      representativeReturns = representativeReturns.clone(); joint = joint.clone(); trainingBars = List.copyOf(trainingBars);
    }
    @Override public double[] representativeReturns() { return representativeReturns.clone(); }
    @Override public double[] joint() { return joint.clone(); }
    public double[] marginal(int horizon) {
      if (horizon != 0 && horizon != 1) throw new IllegalArgumentException("Horizon must be 0 or 1");
      double[] p = new double[3];
      for (int i = 0; i < 9; i++) p[horizon == 0 ? i / 3 : i % 3] += joint[i];
      return p;
    }
  }
  public static int digit(double logReturn, double threshold) {
    return logReturn < -threshold ? -1 : logReturn > threshold ? 1 : 0;
  }
  /** Segments are split before windows, so no raw return is shared across partitions. */
  public static int[][] windows(double[] returns, int from, int to, double threshold) {
    if (from < 0 || to > returns.length || to - from < WIDTH) throw new IllegalArgumentException("Segment too short");
    int[][] rows = new int[to - from - WIDTH + 1][WIDTH];
    for (int i = 0; i < rows.length; i++)
      for (int j = 0; j < WIDTH; j++) rows[i][j] = digit(returns[from + i + j], threshold);
    return rows;
  }
  public static double[] conditional(TernarySpinModel model, int[] context) {
    if (context.length != CONTEXT) throw new IllegalArgumentException("Six context digits required");
    int[] state = Arrays.copyOf(context, WIDTH);
    double[] scores = new double[9]; double max = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < 9; i++) {
      state[6] = i / 3 - 1; state[7] = i % 3 - 1;
      scores[i] = -model.energy(state); max = Math.max(max, scores[i]);
    }
    double total = 0;
    for (int i = 0; i < 9; i++) total += scores[i] = Math.exp(scores[i] - max);
    for (int i = 0; i < 9; i++) scores[i] /= total;
    return scores;
  }
  private static TernarySpinModel independent(int[][] rows) {
    double[] penalties = new double[WIDTH], fields = new double[WIDTH];
    for (int site = 0; site < WIDTH; site++) {
      double[] count = {.5, .5, .5};
      for (int[] row : rows) count[row[site] + 1]++;
      fields[site] = .5 * Math.log(count[2] / count[0]);
      penalties[site] = Math.log(count[1]) - .5 * Math.log(count[0] * count[2]);
      fields[site] = Math.max(-5, Math.min(5, fields[site]));
      penalties[site] = Math.max(-5, Math.min(5, penalties[site]));
    }
    return new TernarySpinModel(new double[WIDTH][WIDTH], penalties, fields);
  }
  private static double loss(TernarySpinModel model, int[][] rows) {
    double sum = 0;
    for (int[] row : rows) {
      if (Thread.currentThread().isInterrupted()) throw new CancellationException();
      double[] p = conditional(model, Arrays.copyOf(row, CONTEXT));
      sum -= Math.log(p[(row[6] + 1) * 3 + row[7] + 1]);
    }
    return sum / rows.length;
  }
  public static Result fit(MarketData.Snapshot snapshot, int maxBars, int epochs) {
    if (maxBars < 150 || maxBars > 1200 || epochs < 1 || epochs > 40)
      throw new IllegalArgumentException("Use 150..1200 bars and 1..40 epochs");
    List<MarketData.Bar> available = snapshot.completed();
    if (available.size() < 150) throw new IllegalArgumentException("Need at least 150 completed bars; choose a shorter interval or longer CSV history");
    List<MarketData.Bar> bars = List.copyOf(available.subList(Math.max(0, available.size() - maxBars), available.size()));
    double[] returns = new double[bars.size() - 1];
    for (int i = 0; i < returns.length; i++) returns[i] = Math.log(bars.get(i + 1).close() / bars.get(i).close());
    int trainEnd = (int) (returns.length * .70), validationEnd = (int) (returns.length * .85);
    double[] absolute = Arrays.stream(Arrays.copyOf(returns, trainEnd)).map(Math::abs).sorted().toArray();
    double threshold = Math.max(1e-8, absolute[(int) Math.floor((absolute.length - 1) / 3.0)]);
    int[][] train = windows(returns, 0, trainEnd, threshold);
    int[][] validation = windows(returns, trainEnd, validationEnd, threshold);
    int[][] test = windows(returns, validationEnd, returns.length, threshold);
    TernarySpinModel baseline = independent(train);
    var options = TernaryTrainingOptions.builder().epochs(epochs).seed(777).build();
    var fit = TernaryBoltzmannTrainer.train(baseline, train, validation, options);
    double networkValidation = loss(fit.model(), validation), baselineValidation = loss(baseline, validation);
    boolean useNetwork = networkValidation < baselineValidation;
    TernarySpinModel selected = useNetwork ? fit.model() : baseline;
    double[] representative = new double[3]; int[] count = new int[3];
    for (int i = 0; i < trainEnd; i++) {
      int category = digit(returns[i], threshold) + 1;
      representative[category] += returns[i]; count[category]++;
    }
    for (int i = 0; i < 3; i++) representative[i] = count[i] == 0 ? (i - 1) * threshold : representative[i] / count[i];
    int[] context = new int[CONTEXT];
    for (int i = 0; i < CONTEXT; i++) context[i] = digit(returns[returns.length - CONTEXT + i], threshold);
    return new Result(selected, threshold, representative, conditional(selected, context), loss(fit.model(), test),
        loss(baseline, test), networkValidation, baselineValidation, useNetwork, train.length, validation.length,
        test.length, fit.selectedEpoch(), fit.estimatedWork(), bars);
  }
}
