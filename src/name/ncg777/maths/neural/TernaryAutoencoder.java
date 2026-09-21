package name.ncg777.maths.neural;

import java.util.*;
import java.util.function.IntConsumer;

/** Small binary-contour autoencoder with a genuinely discrete ternary bottleneck.
 * The decoder receives only the trits, never the continuous encoder activations. */
public final class TernaryAutoencoder {
  private final int width;
  private final double[][] encoder, decoder;
  private final double[] encoderBias, decoderBias = new double[64];

  private TernaryAutoencoder(int width, long seed) {
    this.width = width; encoder = new double[width][64]; decoder = new double[64][width]; encoderBias = new double[width];
    Random random = new Random(seed);
    for (double[] row : encoder) for (int j = 0; j < row.length; j++) row[j] = random.nextGaussian() * .1;
    for (double[] row : decoder) for (int j = 0; j < row.length; j++) row[j] = random.nextGaussian() * .1;
  }

  /** Alternates clean and bit-flip-corrupted inputs, always targeting clean pixels.
   * Hard quantization is used in every forward pass. A tanh straight-through
   * surrogate supplies encoder gradients; it is not the derivative of rounding. */
  public static TernaryAutoencoder train(int[][] clean, int width, int epochs, double noise, long seed, IntConsumer progress) {
    if (width != 8 && width != 16 && width != 32 && width != 64) throw new IllegalArgumentException("Use 8, 16, 32 or 64 trits");
    if (clean == null || clean.length < 1 || clean.length > 1024 || epochs < 1 || epochs > 200
        || (long) clean.length * epochs * width * 64 > 100_000_000)
      throw new IllegalArgumentException("Training exceeds 1024 examples, 200 epochs or its work budget");
    checkNoise(noise); TernarySegmentation.check();
    int[][] data = Arrays.stream(clean).map(row -> { validate(row); return row.clone(); }).toArray(int[][]::new);
    var model = new TernaryAutoencoder(width, seed); Random random = new Random(seed);
    for (int p = 0; p < 64; p++) {
      double ones = 1; for (int[] row : data) ones += row[p];
      double probability = ones / (data.length + 2); model.decoderBias[p] = Math.log(probability / (1 - probability));
    }
    int[] order = new int[data.length]; for (int i = 0; i < order.length; i++) order[i] = i;
    double[] hidden = new double[width], output = new double[64], gradient = new double[width];
    int[] code = new int[width];
    for (int epoch = 0; epoch < epochs; epoch++) {
      for (int i = order.length - 1; i > 0; i--) { int j = random.nextInt(i + 1), t = order[i]; order[i] = order[j]; order[j] = t; }
      double rate = .12 / (1 + epoch * .01);
      for (int index : order) {
        TernarySegmentation.check(); int[] target = data[index];
        int[] input = corrupt(target, epoch % 2 == 0 ? 0 : noise, random);
        model.encode(input, hidden, code); model.decode(code, output); Arrays.fill(gradient, 0);
        for (int p = 0; p < 64; p++) {
          double error = (output[p] - target[p]) / 64;
          for (int k = 0; k < width; k++) {
            gradient[k] += error * model.decoder[p][k];
            model.decoder[p][k] -= rate * error * code[k];
          }
          model.decoderBias[p] -= rate * error;
        }
        for (int k = 0; k < width; k++) {
          double g = gradient[k] * (1 - hidden[k] * hidden[k]); model.encoderBias[k] -= rate * g;
          for (int p = 0; p < 64; p++) model.encoder[k][p] -= rate * g * (2 * input[p] - 1);
        }
      }
      if (progress != null) progress.accept(epoch + 1);
    }
    return model;
  }
  public int width() { return width; }
  public int[] encode(int[] pixels) {
    validate(pixels); TernarySegmentation.check(); int[] code = new int[width]; encode(pixels, new double[width], code); return code;
  }
  public double[] decode(int[] code) {
    if (code == null || code.length != width) throw new IllegalArgumentException("Wrong code width");
    for (int trit : code) if (trit < -1 || trit > 1) throw new IllegalArgumentException("Code must be ternary");
    TernarySegmentation.check(); double[] output = new double[64]; decode(code, output); return output;
  }
  public int[] reconstruct(int[] pixels) { return binary(decode(encode(pixels))); }
  public static int[] binary(double[] probabilities) {
    if (probabilities == null || probabilities.length != 64) throw new IllegalArgumentException("Expected 64 pixel scores");
    int[] result = new int[64];
    for (int i = 0; i < result.length; i++) {
      if (!Double.isFinite(probabilities[i]) || probabilities[i] < 0 || probabilities[i] > 1) throw new IllegalArgumentException("Invalid pixel score");
      result[i] = probabilities[i] >= .5 ? 1 : 0;
    }
    return result;
  }
  private void encode(int[] input, double[] hidden, int[] code) {
    for (int k = 0; k < width; k++) {
      double a = encoderBias[k]; for (int p = 0; p < 64; p++) a += encoder[k][p] * (2 * input[p] - 1);
      hidden[k] = Math.tanh(a); code[k] = hidden[k] < -1.0 / 3 ? -1 : hidden[k] > 1.0 / 3 ? 1 : 0;
    }
  }
  private void decode(int[] code, double[] output) {
    for (int p = 0; p < 64; p++) {
      double a = decoderBias[p]; for (int k = 0; k < width; k++) a += decoder[p][k] * code[k];
      output[p] = a >= 0 ? 1 / (1 + Math.exp(-a)) : Math.exp(a) / (1 + Math.exp(a));
    }
  }
  public static int[] corrupt(int[] clean, double noise, long seed) { validate(clean); checkNoise(noise); return corrupt(clean, noise, new Random(seed)); }
  private static int[] corrupt(int[] clean, double noise, Random random) {
    int[] input = clean.clone(); for (int p = 0; p < 64; p++) if (random.nextDouble() < noise) input[p] = 1 - input[p]; return input;
  }
  private static void checkNoise(double noise) {
    if (!Double.isFinite(noise) || noise < 0 || noise > .5) throw new IllegalArgumentException("Noise must be between 0 and 50%");
  }
  static void validate(int[] pixels) {
    if (pixels == null || pixels.length != 64) throw new IllegalArgumentException("Expected an 8×8 contour");
    for (int p : pixels) if (p != 0 && p != 1) throw new IllegalArgumentException("Contour pixels must be 0 or 1");
  }
}
