package name.ncg777.maths.neural;

import java.util.*;
import java.util.concurrent.CancellationException;
import name.ncg777.maths.Trit;
import name.ncg777.maths.physics.TernarySpinModel;
import name.ncg777.statistics.BoltzmannDistribution;

/** Small exact recall experiment comparing learned probabilities with stored patterns. */
public final class TernaryAssociativeMemory {
  public static final int MAX_WIDTH = 6;
  public static final int MAX_ROWS = 1000;
  private final int width;
  private final int[][] patterns;
  private final int[][] states;
  private final int[] counts;
  private final double[] energies;

  public record Cue(int[] values, boolean[] known) {
    public Cue {
      values = values.clone(); known = known.clone();
      if (values.length != known.length || values.length < 1 || values.length > MAX_WIDTH)
        throw new IllegalArgumentException("Cue must have 1 to 6 positions and a matching known-value mask");
      for (int value : values) Trit.BUF(value);
    }
    @Override public int[] values() { return values.clone(); }
    @Override public boolean[] known() { return known.clone(); }
  }
  public record Candidate(String pattern, double weight, int mismatches, boolean stored) {}
  public record Recall(List<Candidate> spin, List<Candidate> nearest) {
    public Recall { spin = List.copyOf(spin); nearest = List.copyOf(nearest); }
  }
  public record Benchmark(int trials, int spinCorrect, int nearestCorrect, int spinTies, int nearestTies) {}

  private TernaryAssociativeMemory(int[][] patterns, TernarySpinModel model, double beta) {
    width = model.size();
    this.patterns = Arrays.stream(patterns).map(int[]::clone).toArray(int[][]::new);
    int size = (int) Math.pow(3, width);
    counts = new int[size]; states = new int[size][width]; energies = new double[size];
    for (int[] row : patterns) counts[encode(row)]++;
    for (int code = 0; code < size; code++) {
      int digits = code;
      for (int i = width - 1; i >= 0; i--) { states[code][i] = digits % 3 - 1; digits /= 3; }
      energies[code] = beta * model.energy(states[code]);
    }
  }

  public static TernaryAssociativeMemory train(int[][] rows, int epochs) {
    validate(rows);
    if (epochs < 1 || epochs > 500) throw new IllegalArgumentException("Use 1 to 500 epochs");
    int n = rows[0].length;
    int[][] copy = Arrays.stream(rows).map(int[]::clone).toArray(int[][]::new);
    var initial = new TernarySpinModel(new double[n][n], new double[n], new double[n]);
    var options = TernaryTrainingOptions.builder().epochs(epochs).learningRate(0.1)
        .maxExactStates(729).build();
    var fit = TernaryBoltzmannTrainer.train(initial, copy, options);
    return new TernaryAssociativeMemory(copy, fit.model(), fit.beta());
  }

  /** Useful for evaluating a supplied model without fitting it again. */
  public static TernaryAssociativeMemory fromModel(int[][] rows, TernarySpinModel model, double beta) {
    validate(rows); Objects.requireNonNull(model);
    if (model.size() != rows[0].length || !Double.isFinite(beta) || beta <= 0)
      throw new IllegalArgumentException("Model width and positive temperature scale must match");
    return new TernaryAssociativeMemory(rows, model, beta);
  }
  public int width() { return width; }

  /**
   * errorRate=0 clamps known values. Otherwise each observed value has probability
   * 1-errorRate of being correct and errorRate/2 of being either other trit.
   * Unknown positions supply no evidence. Nearest matching uses observed Hamming distance.
   */
  public Recall recall(Cue cue, double errorRate) {
    Objects.requireNonNull(cue);
    if (cue.values.length != width) throw new IllegalArgumentException("Cue width must be " + width);
    if (!Double.isFinite(errorRate) || errorRate < 0 || errorRate > 0.5)
      throw new IllegalArgumentException("Observation error rate must be between 0 and 0.5");
    double[] costs = energies.clone();
    int[] distances = new int[states.length];
    int minimum = Integer.MAX_VALUE;
    for (int code = 0; code < states.length; code++) {
      cancelled();
      for (int i = 0; i < width; i++) if (cue.known[i]) {
        boolean match = cue.values[i] == states[code][i];
        if (!match) distances[code]++;
        costs[code] += errorRate == 0 ? (match ? 0 : Double.POSITIVE_INFINITY)
            : -Math.log(match ? 1 - errorRate : errorRate / 2);
      }
      if (counts[code] > 0) minimum = Math.min(minimum, distances[code]);
    }
    var distribution = new BoltzmannDistribution(costs, 1);
    int total = 0;
    for (int code = 0; code < counts.length; code++) if (distances[code] == minimum) total += counts[code];
    List<Candidate> spin = new ArrayList<>(), nearest = new ArrayList<>();
    for (int code = 0; code < states.length; code++) {
      double probability = distribution.probability(code);
      if (probability > 0) spin.add(new Candidate(format(states[code]), probability, distances[code], counts[code] > 0));
      if (counts[code] > 0 && distances[code] == minimum)
        nearest.add(new Candidate(format(states[code]), (double) counts[code] / total, distances[code], true));
    }
    // Stable sorting keeps enumeration order T,0,1 for exactly equal weights.
    Comparator<Candidate> order = Comparator.comparingDouble(Candidate::weight).reversed();
    spin.sort(order); nearest.sort(order);
    return new Recall(spin, nearest);
  }

  /** Tests recall of the training memories under fresh damage, not unseen-data generalization. */
  public Benchmark benchmark(int trials, int hidden, double errorRate, long seed) {
    if (trials < 1 || trials > 500 || hidden < 0 || hidden > width)
      throw new IllegalArgumentException("Use 1..500 trials and 0..width hidden positions");
    if (!Double.isFinite(errorRate) || errorRate < 0 || errorRate > 0.5)
      throw new IllegalArgumentException("Observation error rate must be between 0 and 0.5");
    Random random = new Random(seed);
    int spinCorrect = 0, nearestCorrect = 0, spinTies = 0, nearestTies = 0;
    for (int trial = 0; trial < trials; trial++) {
      cancelled();
      int[] target = patterns[random.nextInt(patterns.length)], values = target.clone();
      boolean[] known = new boolean[width]; Arrays.fill(known, true);
      List<Integer> positions = new ArrayList<>();
      for (int i = 0; i < width; i++) positions.add(i);
      Collections.shuffle(positions, random);
      for (int i = 0; i < hidden; i++) known[positions.get(i)] = false;
      for (int i = 0; i < width; i++) if (known[i] && random.nextDouble() < errorRate)
        values[i] = (values[i] + 1 + 1 + random.nextInt(2)) % 3 - 1;
      Recall result = recall(new Cue(values, known), errorRate);
      String expected = format(target);
      if (result.spin.get(0).pattern.equals(expected)) spinCorrect++;
      if (result.nearest.get(0).pattern.equals(expected)) nearestCorrect++;
      if (tied(result.spin)) spinTies++;
      if (tied(result.nearest)) nearestTies++;
    }
    return new Benchmark(trials, spinCorrect, nearestCorrect, spinTies, nearestTies);
  }
  private static boolean tied(List<Candidate> candidates) {
    return candidates.size() > 1 && Math.abs(candidates.get(0).weight - candidates.get(1).weight) < 1e-10;
  }
  private static void cancelled() { if (Thread.currentThread().isInterrupted()) throw new CancellationException(); }
  private static int encode(int[] row) { int code = 0; for (int value : row) code = 3 * code + value + 1; return code; }
  private static void validate(int[][] rows) {
    Objects.requireNonNull(rows);
    if (rows.length < 1 || rows.length > MAX_ROWS || rows[0] == null || rows[0].length < 1 || rows[0].length > MAX_WIDTH)
      throw new IllegalArgumentException("Use 1..1000 patterns, each with 1..6 trits");
    for (int[] row : rows) {
      if (row == null || row.length != rows[0].length) throw new IllegalArgumentException("All patterns must have the same width");
      for (int value : row) Trit.BUF(value);
    }
  }
  public static int[][] parsePatterns(String text) {
    Objects.requireNonNull(text);
    if (text.length() > 20000) throw new IllegalArgumentException("Pattern text is too long");
    List<int[]> rows = new ArrayList<>();
    for (String line : text.split("\\R")) if (!line.isBlank()) {
      if (line.contains("?")) throw new IllegalArgumentException("Stored patterns must be complete; ? is only for cues");
      rows.add(parseCue(line).values());
    }
    int[][] result = rows.toArray(int[][]::new); validate(result); return result;
  }
  public static Cue parseCue(String text) {
    Objects.requireNonNull(text);
    if (text.length() > 100) throw new IllegalArgumentException("Cue text is too long");
    String compact = text.replaceAll("\\s", "");
    if (compact.length() < 1 || compact.length() > MAX_WIDTH) throw new IllegalArgumentException("Use 1..6 positions: T, 0, 1, or ?");
    int[] values = new int[compact.length()]; boolean[] known = new boolean[values.length];
    for (int i = 0; i < values.length; i++) {
      char value = compact.charAt(i);
      known[i] = value != '?';
      values[i] = switch (value) {
        case 'T', 't' -> -1; case '0', '?' -> 0; case '1' -> 1;
        default -> throw new IllegalArgumentException("Use T for -1, 0, 1, and ? for unknown");
      };
    }
    return new Cue(values, known);
  }
  public static String format(int[] values) {
    StringBuilder text = new StringBuilder();
    for (int value : values) { Trit.BUF(value); text.append(value == -1 ? 'T' : value == 0 ? '0' : '1'); }
    return text.toString();
  }
}
