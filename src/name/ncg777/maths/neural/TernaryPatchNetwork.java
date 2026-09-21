package name.ncg777.maths.neural;

import java.util.*;
import java.util.function.IntConsumer;
import name.ncg777.maths.neural.TernaryContours.Raster;
import name.ncg777.maths.neural.TernarySegmentation.Example;

/** Shared 8×8 convolutional filters restore the central 4×4 contour patch.
 * Stride-two inference averages four overlapping predictions per pixel. The
 * network predicts local boundaries only; enclosure is a separate global step. */
public final class TernaryPatchNetwork {
  public static final int PATCH = 8, OUTPUT_SIDE = 4, STRIDE = 2;
  private static final int FILTERS = 16, OUTPUTS = 16;
  private final double[][] weights = new double[FILTERS][64], output = new double[OUTPUTS][FILTERS];
  private final double[] bias = new double[FILTERS], outputBias = new double[OUTPUTS];

  private TernaryPatchNetwork(long seed) {
    Random random = new Random(seed);
    for (int f = 0; f < FILTERS; f++) for (int j = 0; j < 64; j++) weights[f][j] = random.nextGaussian() * .12;
    for (int k = 0; k < OUTPUTS; k++) {
      outputBias[k] = -2;
      for (int f = 0; f < FILTERS; f++) output[k][f] = random.nextGaussian() * .05;
    }
  }
  private record Patch(double[] input, boolean[] target) {}

  /** Samples 96 windows per image. Caps: 128 images, 50 epochs and 150,000
   * window updates. No evaluation images participate in fitting. */
  public static TernaryPatchNetwork train(List<Example> examples, int epochs, long seed, IntConsumer progress) {
    if (examples == null || examples.isEmpty() || examples.size() > 128 || epochs < 1 || epochs > 50
        || (long) examples.size() * 96 * epochs > 150_000)
      throw new IllegalArgumentException("Training exceeds 128 images, 50 epochs or 150,000 patch updates");
    TernarySegmentation.check();
    List<Patch> patches = new ArrayList<>(); Random random = new Random(seed);
    for (Example example : examples) {
      Raster image = example.observed(); boolean[] target = example.boundary();
      if (image.width() < 8 || image.height() < 8) throw new IllegalArgumentException("Training images must be at least 8×8");
      for (int n = 0; n < 96; n++) {
        int x = random.nextInt(image.width()) - 3, y = random.nextInt(image.height()) - 3;
        double[] input = new double[64]; readPatch(image, x, y, input); boolean[] labels = new boolean[OUTPUTS];
        for (int k = 0; k < OUTPUTS; k++) {
          int px = x + 2 + k % 4, py = y + 2 + k / 4;
          labels[k] = px >= 0 && py >= 0 && px < image.width() && py < image.height() && target[py * image.width() + px];
        }
        patches.add(new Patch(input, labels));
      }
    }
    var network = new TernaryPatchNetwork(seed);
    double[] hidden = new double[FILTERS], probabilities = new double[OUTPUTS], gradient = new double[FILTERS];
    for (int epoch = 0; epoch < epochs; epoch++) {
      Collections.shuffle(patches, random); double rate = .08 / (1 + .03 * epoch);
      for (Patch patch : patches) {
        TernarySegmentation.check(); network.forward(patch.input, hidden, probabilities); Arrays.fill(gradient, 0);
        for (int k = 0; k < OUTPUTS; k++) {
          // Positive weighting counters sparse contours; outputs are scores, not calibrated probabilities.
          double error = (probabilities[k] - (patch.target[k] ? 1 : 0)) * (patch.target[k] ? 3 : 1) / OUTPUTS;
          for (int f = 0; f < FILTERS; f++) {
            gradient[f] += error * network.output[k][f];
            network.output[k][f] -= rate * error * hidden[f];
          }
          network.outputBias[k] -= rate * error;
        }
        for (int f = 0; f < FILTERS; f++) if (hidden[f] > 0) {
          network.bias[f] -= rate * gradient[f];
          for (int j = 0; j < 64; j++) network.weights[f][j] -= rate * gradient[f] * patch.input[j];
        }
      }
      if (progress != null) progress.accept(epoch + 1);
    }
    return network;
  }

  public record Prediction(int width, int height, double[] scores, double[] disagreement, int[] votes) {
    public Prediction {
      TernarySegmentation.dimensions(width, height, scores.length);
      if (disagreement.length != scores.length || votes.length != scores.length) throw new IllegalArgumentException("Mismatched prediction arrays");
      scores = scores.clone(); disagreement = disagreement.clone(); votes = votes.clone();
      for (int i = 0; i < scores.length; i++) if (!Double.isFinite(scores[i]) || scores[i] < 0 || scores[i] > 1
          || !Double.isFinite(disagreement[i]) || disagreement[i] < 0 || disagreement[i] > .5 + 1e-12 || votes[i] < 1)
        throw new IllegalArgumentException("Invalid prediction score, disagreement or vote count");
    }
    @Override public double[] scores() { return scores.clone(); }
    @Override public double[] disagreement() { return disagreement.clone(); }
    @Override public int[] votes() { return votes.clone(); }
    public boolean[] threshold(double threshold) {
      if (!Double.isFinite(threshold) || threshold < 0 || threshold > 1) throw new IllegalArgumentException("Threshold must be 0–1");
      boolean[] result = new boolean[scores.length];
      for (int i = 0; i < result.length; i++) result[i] = scores[i] >= threshold;
      return result;
    }
    public double meanDisagreement() { return Arrays.stream(disagreement).average().orElse(0); }
  }

  public Prediction predict(Raster image) {
    int width = image.width(), height = image.height();
    double[] sum = new double[width * height], squares = new double[sum.length]; int[] votes = new int[sum.length];
    double[] input = new double[64], hidden = new double[FILTERS], probabilities = new double[OUTPUTS];
    for (int y = -4; y < height - 2; y += STRIDE) {
      TernarySegmentation.check();
      for (int x = -4; x < width - 2; x += STRIDE) {
        readPatch(image, x, y, input); forward(input, hidden, probabilities);
        for (int k = 0; k < OUTPUTS; k++) {
          int px = x + 2 + k % 4, py = y + 2 + k / 4;
          if (px < 0 || py < 0 || px >= width || py >= height) continue;
          int i = py * width + px; sum[i] += probabilities[k]; squares[i] += probabilities[k] * probabilities[k]; votes[i]++;
        }
      }
    }
    for (int i = 0; i < sum.length; i++) {
      sum[i] /= votes[i]; squares[i] = Math.sqrt(Math.max(0, squares[i] / votes[i] - sum[i] * sum[i]));
    }
    return new Prediction(width, height, sum, squares, votes);
  }
  private static void readPatch(Raster image, int x, int y, double[] input) {
    for (int dy = 0; dy < PATCH; dy++) for (int dx = 0; dx < PATCH; dx++) input[dy * PATCH + dx] = image.at(x + dx, y + dy) == 0 ? 0 : 1;
  }
  private void forward(double[] input, double[] hidden, double[] probabilities) {
    for (int f = 0; f < FILTERS; f++) {
      double a = bias[f]; for (int j = 0; j < 64; j++) a += weights[f][j] * input[j]; hidden[f] = Math.max(0, a);
    }
    for (int k = 0; k < OUTPUTS; k++) {
      // Fixed identity skip starts from the observed central contour; the learned filters supply corrections.
      double logit = outputBias[k] + 4 * input[(k / 4 + 2) * PATCH + k % 4 + 2];
      for (int f = 0; f < FILTERS; f++) logit += output[k][f] * hidden[f];
      probabilities[k] = logit >= 0 ? 1 / (1 + Math.exp(-logit)) : Math.exp(logit) / (1 + Math.exp(logit));
    }
  }
}
