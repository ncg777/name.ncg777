package name.ncg777.maths.neural;

import java.util.*;
import java.util.function.IntConsumer;

/** Fixed disjoint synthetic split and shared baselines for ternary bottleneck sizes. */
public final class TernaryEmbeddingExperiment {
  private TernaryEmbeddingExperiment() {}
  public record Dataset(int[][] training, int[][] testing) {
    public Dataset { training = copy(training); testing = copy(testing); }
    @Override public int[][] training() { return copy(training); }
    @Override public int[][] testing() { return copy(testing); }
    private static int[][] copy(int[][] rows) { return Arrays.stream(rows).map(r -> { TernaryAutoencoder.validate(r); return r.clone(); }).toArray(int[][]::new); }
  }
  public static Dataset dataset() {
    Random random = new Random(777); Set<String> seen = new HashSet<>(); List<int[]> rows = new ArrayList<>();
    for (int attempt = 0; rows.size() < 256 && attempt < 10000; attempt++) {
      TernarySegmentation.check(); boolean[] boundary = TernaryContours.boundaries(TernaryContours.generate(8, random.nextLong()));
      int[] row = new int[64]; for (int p = 0; p < row.length; p++) row[p] = boundary[p] ? 1 : 0;
      if (Arrays.stream(row).sum() > 0 && seen.add(Arrays.toString(row))) rows.add(row);
    }
    if (rows.size() != 256) throw new IllegalStateException("Insufficient unique contours");
    return new Dataset(rows.subList(0, 192).toArray(int[][]::new), rows.subList(192, 256).toArray(int[][]::new));
  }
  public record Score(long errors, long pixels, long tp, long fp, long fn, int exact, int examples) {
    public double errorRate() { return pixels == 0 ? 0 : (double) errors / pixels; }
    public double f1() { long n = 2 * tp + fp + fn; return n == 0 ? 1 : 2.0 * tp / n; }
    public double exactRate() { return examples == 0 ? 0 : (double) exact / examples; }
  }
  public record Row(String method, String input, Score score, double codeChange) {}
  public record Fit(Map<Integer, TernaryAutoencoder> models, List<Row> rows) {
    public Fit { models = Collections.unmodifiableMap(new LinkedHashMap<>(models)); rows = List.copyOf(rows); }
  }
  public static Fit train(Dataset data, IntConsumer progress) {
    Map<Integer, TernaryAutoencoder> models = new LinkedHashMap<>();
    for (int width : new int[] {8, 16, 32, 64}) {
      models.put(width, TernaryAutoencoder.train(data.training, width, 80, .05, 777, null));
      if (progress != null) progress.accept(width);
    }
    return new Fit(models, evaluate(data, models));
  }
  public static List<Row> evaluate(Dataset data, Map<Integer, TernaryAutoencoder> models) {
    List<Row> rows = new ArrayList<>(); int[] mean = mean(data.training);
    for (double noise : new double[] {0, .05}) {
      String condition = noise == 0 ? "Clean" : "5% bit flips";
      Map<String, List<int[]>> predictions = new LinkedHashMap<>();
      for (String method : List.of("Copy input", "Mean training image", "Nearest training image")) predictions.put(method, new ArrayList<>());
      for (int width : models.keySet()) predictions.put(width + " trits", new ArrayList<>());
      Map<Integer, Long> changes = new HashMap<>();
      for (int n = 0; n < data.testing.length; n++) {
        TernarySegmentation.check(); int[] clean = data.testing[n], input = TernaryAutoencoder.corrupt(clean, noise, 10000 + n);
        predictions.get("Copy input").add(input); predictions.get("Mean training image").add(mean);
        predictions.get("Nearest training image").add(data.training[nearest(input, data.training)]);
        for (var entry : models.entrySet()) {
          int[] code = entry.getValue().encode(input);
          predictions.get(entry.getKey() + " trits").add(TernaryAutoencoder.binary(entry.getValue().decode(code)));
          changes.merge(entry.getKey(), (long) distance(code, entry.getValue().encode(clean)), Long::sum);
        }
      }
      for (var entry : predictions.entrySet()) {
        int width = entry.getKey().endsWith(" trits") ? Integer.parseInt(entry.getKey().split(" ")[0]) : 0;
        double change = width == 0 ? Double.NaN : (double) changes.get(width) / (data.testing.length * width);
        rows.add(new Row(entry.getKey(), condition, score(data.testing, entry.getValue().toArray(int[][]::new)), change));
      }
    }
    return List.copyOf(rows);
  }
  public static Score score(int[][] targets, int[][] predictions) {
    if (targets.length != predictions.length) throw new IllegalArgumentException("Mismatched examples");
    long errors = 0, tp = 0, fp = 0, fn = 0; int exact = 0;
    for (int n = 0; n < targets.length; n++) {
      TernaryAutoencoder.validate(targets[n]); TernaryAutoencoder.validate(predictions[n]); int wrong = 0;
      for (int i = 0; i < 64; i++) {
        int a = targets[n][i], b = predictions[n][i];
        if (a != b) wrong++; if (a == 1 && b == 1) tp++; else if (b == 1) fp++; else if (a == 1) fn++;
      }
      errors += wrong; if (wrong == 0) exact++;
    }
    return new Score(errors, targets.length * 64L, tp, fp, fn, exact, targets.length);
  }
  public static int nearest(int[] query, int[][] rows) {
    if (rows.length == 0) throw new IllegalArgumentException("No neighbours");
    int best = 0, distance = Integer.MAX_VALUE;
    for (int i = 0; i < rows.length; i++) { int d = distance(query, rows[i]); if (d < distance) { best = i; distance = d; } }
    return best;
  }
  public static int distance(int[] a, int[] b) {
    if (a.length != b.length) throw new IllegalArgumentException("Mismatched vector widths");
    int distance = 0; for (int i = 0; i < a.length; i++) if (a[i] != b[i]) distance++; return distance;
  }
  private static int[] mean(int[][] rows) {
    if (rows.length == 0) throw new IllegalArgumentException("Empty training set");
    int[] result = new int[64]; for (int[] row : rows) for (int i = 0; i < 64; i++) result[i] += row[i];
    for (int i = 0; i < 64; i++) result[i] = result[i] * 2 >= rows.length ? 1 : 0; return result;
  }
}
