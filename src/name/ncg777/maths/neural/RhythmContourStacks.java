package name.ncg777.maths.neural;

import java.util.*;
import java.util.function.IntConsumer;
import name.ncg777.maths.neural.RhythmContourEmbeddings.Example;
import name.ncg777.maths.numbers.*;
import name.ncg777.maths.numbers.predicates.ShadowContourIsomorphic;
import name.ncg777.maths.numbers.relations.PredicatedDifferences;
import name.ncg777.maths.sequences.Sequence;

/** Four SCI bars, not 64 separate rhythms: each contour supplies sixteen samples. */
public final class RhythmContourStacks {
  private RhythmContourStacks() {}
  public record Stack(List<String> hex, double[] values) {
    public Stack {
      hex = List.copyOf(hex); values = values.clone(); if (hex.size() != 4 || values.length != 64) throw new IllegalArgumentException("Expected four bars with sixteen samples each");
      for (double value : values) if (!Double.isFinite(value) || value < -1 || value > 1) throw new IllegalArgumentException("Invalid contour height");
    }
    @Override public double[] values() { return values.clone(); }
  }
  public record Dataset(List<Stack> training, List<Stack> testing) {
    public Dataset { training = List.copyOf(training); testing = List.copyOf(testing); }
  }
  public static Dataset dataset(IntConsumer progress) {
    var catalogue = RhythmContourEmbeddings.catalogue(progress); Set<String> used = new HashSet<>();
    return new Dataset(generate(catalogue.training(), 192, 777, used), generate(catalogue.testing(), 64, 778, used));
  }
  private static List<Stack> generate(List<Example> pool, int count, long seed, Set<String> used) {
    int n = pool.size(); boolean[][] compatible = new boolean[n][n]; BinaryNatural[] rhythms = new BinaryNatural[n]; double[][] contours = new double[n][];
    var difference = new PredicatedDifferences(new ShadowContourIsomorphic());
    for (int i = 0; i < n; i++) { rhythms[i] = new Natural(Cipher.Name.Hexadecimal, pool.get(i).hex()).toBinaryNatural(); contours[i] = samples(pool.get(i)); }
    for (int i = 0; i < n; i++) {
      TernarySegmentation.check(); for (int j = 0; j < n; j++) compatible[i][j] = difference.test(rhythms[i], rhythms[j]);
    }
    Random random = new Random(seed); List<Stack> result = new ArrayList<>();
    for (int attempt = 0; result.size() < count && attempt < 20000; attempt++) {
      TernarySegmentation.check(); int[] selected = new int[4];
      for (int row = 0; row < 4; row++) {
        List<Integer> candidates = new ArrayList<>();
        for (int i = 0; i < n; i++) {
          boolean valid = true; for (int previous = 0; previous < row; previous++) valid &= compatible[i][selected[previous]];
          if (valid) candidates.add(i);
        }
        // Repeating a prior bar is allowed and has empty (hence SCI) differences.
        if (candidates.isEmpty()) throw new IllegalStateException("No compatible SCI bar");
        selected[row] = candidates.get(random.nextInt(candidates.size()));
      }
      double[] values = new double[64]; List<String> hex = new ArrayList<>();
      for (int row = 0; row < 4; row++) { System.arraycopy(contours[selected[row]], 0, values, row * 16, 16); hex.add(pool.get(selected[row]).hex()); }
      if (used.add(Arrays.toString(values))) result.add(new Stack(hex, values));
    }
    if (result.size() != count) throw new IllegalStateException("Not enough distinct compatible contour stacks within the search budget");
    return result;
  }
  /** Scale the existing ordinal contour sequence to a bar's 16 equally spaced
   * positions. These are contour samples, not reconstructed onset times. */
  public static double[] samples(Example example) {
    Sequence path = Sequence.parse(example.path()); if (path.isEmpty()) throw new IllegalArgumentException("Empty contour");
    double min = path.getMin(), range = path.getMax() - min; double[] samples = new double[16];
    for (int step = 0; step < 16; step++) {
      double x = step * (path.size() - 1.0) / 15; int a = (int) Math.floor(x), b = Math.min(a + 1, path.size() - 1);
      double height = path.get(a) * (1 - (x - a)) + path.get(b) * (x - a);
      samples[step] = range == 0 ? 0 : 2 * (height - min) / range - 1;
    }
    return samples;
  }
}
