package name.ncg777.maths.neural;

import java.util.*;

/** Continuous contour heights, discrete ternary bottleneck, continuous decoded heights. */
public final class TernaryCurveAutoencoder {
  private final int width;
  private final double[][] encoder, decoder;
  private final double[] bias, outputBias = new double[64];
  private TernaryCurveAutoencoder(int width, long seed) {
    this.width = width; encoder = new double[width][64]; decoder = new double[64][width]; bias = new double[width];
    Random random = new Random(seed);
    for (double[] row : encoder) for (int i = 0; i < row.length; i++) row[i] = random.nextGaussian() * .12;
    for (double[] row : decoder) for (int i = 0; i < row.length; i++) row[i] = random.nextGaussian() * .12;
  }
  public static TernaryCurveAutoencoder train(double[][] examples, int width, int epochs, long seed) {
    if (width != 8 && width != 16 && width != 32 && width != 64) throw new IllegalArgumentException("Use 8, 16, 32 or 64 trits");
    if (examples.length < 1 || examples.length > 1024 || epochs < 1 || epochs > 200 || (long) examples.length * epochs * width * 64 > 100_000_000)
      throw new IllegalArgumentException("Curve training exceeds its work budget");
    TernarySegmentation.check(); double[][] data = Arrays.stream(examples).map(a -> { validate(a); return a.clone(); }).toArray(double[][]::new);
    var model = new TernaryCurveAutoencoder(width, seed); Random random = new Random(seed); int[] order = new int[data.length];
    for (int i = 0; i < order.length; i++) order[i] = i;
    double[] hidden = new double[width], gradient = new double[width]; int[] code = new int[width];
    for (int epoch = 0; epoch < epochs; epoch++) {
      for (int i = order.length - 1; i > 0; i--) { int j = random.nextInt(i + 1), t = order[i]; order[i] = order[j]; order[j] = t; }
      double rate = .12 / (1 + .01 * epoch);
      for (int index : order) {
        TernarySegmentation.check(); double[] input = noise(data[index], epoch % 2 == 0 ? 0 : .05, random);
        model.encode(input, hidden, code); double[] output = model.decode(code); Arrays.fill(gradient, 0);
        for (int p = 0; p < 64; p++) {
          double error = 2 * (output[p] - data[index][p]) * (1 - output[p] * output[p]) / 64;
          for (int k = 0; k < width; k++) { gradient[k] += error * model.decoder[p][k]; model.decoder[p][k] -= rate * error * code[k]; }
          model.outputBias[p] -= rate * error;
        }
        for (int k = 0; k < width; k++) {
          double g = gradient[k] * (1 - hidden[k] * hidden[k]); model.bias[k] -= rate * g;
          for (int p = 0; p < 64; p++) model.encoder[k][p] -= rate * g * input[p];
        }
      }
    }
    return model;
  }
  public int[] encode(double[] values) { validate(values); TernarySegmentation.check(); int[] code = new int[width]; encode(values, new double[width], code); return code; }
  private void encode(double[] values, double[] hidden, int[] code) {
    for (int k = 0; k < width; k++) {
      double a = bias[k]; for (int p = 0; p < 64; p++) a += encoder[k][p] * values[p];
      hidden[k] = Math.tanh(a); code[k] = hidden[k] < -1.0 / 3 ? -1 : hidden[k] > 1.0 / 3 ? 1 : 0;
    }
  }
  public double[] decode(int[] code) {
    if (code.length != width) throw new IllegalArgumentException("Wrong code width");
    for (int t : code) if (t < -1 || t > 1) throw new IllegalArgumentException("Code must be ternary");
    TernarySegmentation.check(); double[] result = new double[64];
    for (int p = 0; p < 64; p++) { double a = outputBias[p]; for (int k = 0; k < width; k++) a += decoder[p][k] * code[k]; result[p] = Math.tanh(a); }
    return result;
  }
  public static double[] noise(double[] input, double fraction, long seed) { validate(input); if (!Double.isFinite(fraction) || fraction < 0 || fraction > .5) throw new IllegalArgumentException("Noise must be 0–50%"); return noise(input, fraction, new Random(seed)); }
  private static double[] noise(double[] input, double fraction, Random random) {
    double[] result = input.clone(); for (int i = 0; i < 64; i++) result[i] = Math.max(-1, Math.min(1, result[i] + random.nextGaussian() * fraction * 2)); return result;
  }
  public static double error(double[] a, double[] b) { validate(a); validate(b); double error = 0; for (int i = 0; i < 64; i++) error += Math.pow((a[i] - b[i]) / 2, 2); return error / 64; }
  private static void validate(double[] values) {
    if (values == null || values.length != 64) throw new IllegalArgumentException("Expected four contours × sixteen heights");
    for (double v : values) if (!Double.isFinite(v) || v < -1 || v > 1) throw new IllegalArgumentException("Heights must be finite and within −1..1");
  }
}
