package name.ncg777.maths.neural;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.function.IntConsumer;
import name.ncg777.maths.neural.TernaryContours.Raster;

/** Tiny CPU convolutional experiment: 2 input channels, 8 shared 3x3 ReLU filters,
 * then a shared 1x1 sigmoid output. No dense layer or fixed image size. */
public final class TernaryEdgeNetwork {
  private static final int FILTERS = 8, INPUTS = 18;
  private final double[][] weights = new double[FILTERS][INPUTS];
  private final double[] biases = new double[FILTERS], output = new double[FILTERS];
  private double outputBias;

  private TernaryEdgeNetwork(long seed) {
    Random random = new Random(seed);
    for (int f = 0; f < FILTERS; f++) {
      output[f] = random.nextGaussian() * .25;
      for (int j = 0; j < INPUTS; j++) weights[f][j] = random.nextGaussian() * .3;
    }
  }

  /** Fits a new network; bounds cap CPU use. Caller retains ownership of its dataset. */
  public static TernaryEdgeNetwork train(List<Raster> images, int epochs, long seed, IntConsumer progress) {
    if (images == null || images.isEmpty() || images.size() > 256 || epochs < 1 || epochs > 100)
      throw new IllegalArgumentException("Use 1–256 images and 1–100 epochs");
    long pixels = 0;
    for (Raster image : images) pixels += (long) image.width() * image.height();
    if (pixels * epochs > 2_000_000) throw new IllegalArgumentException("Training exceeds two million pixel updates");
    check();
    TernaryEdgeNetwork network = new TernaryEdgeNetwork(seed);
    List<Raster> shuffled = new ArrayList<>(images); Random random = new Random(seed);
    double[] input = new double[INPUTS], hidden = new double[FILTERS];
    for (int epoch = 0; epoch < epochs; epoch++) {
      Collections.shuffle(shuffled, random);
      double rate = .025 / (1 + epoch * .025);
      for (Raster image : shuffled) {
        check(); boolean[] labels = TernaryContours.boundaries(image);
        int[] order = new int[labels.length];
        for (int i = 0; i < order.length; i++) order[i] = i;
        for (int i = order.length - 1; i > 0; i--) { int j = random.nextInt(i + 1), t = order[i]; order[i] = order[j]; order[j] = t; }
        for (int i : order) {
          if ((i & 127) == 0) check();
          patch(image, i % image.width(), i / image.width(), input);
          double prediction = network.forward(input, hidden);
          // Sigmoid + binary cross entropy: derivative with respect to the logit.
          double error = prediction - (labels[i] ? 1 : 0);
          for (int f = 0; f < FILTERS; f++) {
            double gradient = hidden[f] > 0 ? error * network.output[f] : 0;
            network.output[f] -= rate * error * hidden[f];
            network.biases[f] -= rate * gradient;
            for (int j = 0; j < INPUTS; j++) network.weights[f][j] -= rate * gradient * input[j];
          }
          network.outputBias -= rate * error;
        }
      }
      if (progress != null) progress.accept(epoch + 1);
    }
    return network;
  }

  /** Each overlapping 3x3 receptive field contributes one central prediction; no seams. */
  public double[] predict(Raster image) {
    double[] result = new double[image.width() * image.height()];
    double[] input = new double[INPUTS], hidden = new double[FILTERS];
    for (int y = 0; y < image.height(); y++) {
      check();
      for (int x = 0; x < image.width(); x++) {
        patch(image, x, y, input); result[y * image.width() + x] = forward(input, hidden);
      }
    }
    return result;
  }

  public record Score(long truePositive, long falsePositive, long falseNegative) {
    public double precision() { return truePositive + falsePositive == 0 ? 0 : (double) truePositive / (truePositive + falsePositive); }
    public double recall() { return truePositive + falseNegative == 0 ? 0 : (double) truePositive / (truePositive + falseNegative); }
    public double f1() { long n = 2 * truePositive + falsePositive + falseNegative; return n == 0 ? 0 : 2.0 * truePositive / n; }
  }

  public Score evaluate(List<Raster> images) {
    long tp = 0, fp = 0, fn = 0;
    for (Raster image : images) {
      boolean[] target = TernaryContours.boundaries(image); double[] prediction = predict(image);
      for (int i = 0; i < target.length; i++) {
        if (prediction[i] >= .5) { if (target[i]) tp++; else fp++; }
        else if (target[i]) fn++;
      }
    }
    return new Score(tp, fp, fn);
  }

  private double forward(double[] input, double[] hidden) {
    double logit = outputBias;
    for (int f = 0; f < FILTERS; f++) {
      double a = biases[f];
      for (int j = 0; j < INPUTS; j++) a += weights[f][j] * input[j];
      hidden[f] = Math.max(0, a); logit += output[f] * hidden[f];
    }
    return logit >= 0 ? 1 / (1 + Math.exp(-logit)) : Math.exp(logit) / (1 + Math.exp(logit));
  }

  private static void patch(Raster image, int x, int y, double[] result) {
    int k = 0;
    for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) {
      int p = image.at(x + dx, y + dy);
      result[k] = p; result[9 + k] = p == 0 ? 0 : 1; k++;
    }
  }
  private static void check() { if (Thread.currentThread().isInterrupted()) throw new CancellationException("Cancelled"); }
}
