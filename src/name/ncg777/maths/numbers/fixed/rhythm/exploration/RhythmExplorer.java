package name.ncg777.maths.numbers.fixed.rhythm.exploration;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.function.Predicate;
import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmClauseParser;
import name.ncg777.maths.numbers.predicates.Euclidean;

/** UI-independent, bounded completion and reproducible local evolution. No full rhythm graph. */
public final class RhythmExplorer {
  private RhythmExplorer() {}
  public enum Stop { EXHAUSTED, RESULT_LIMIT, BUDGET }
  public record Search(List<String> candidates, int attempts, Stop stop) {
    public Search { candidates = List.copyOf(candidates); }
  }
  public static void check() {
    if (Thread.currentThread().isInterrupted()) throw new CancellationException("Stopped.");
  }
  public static Predicate<BinaryNatural> rule(String clause, int steps) {
    // Reject incompatible ordinal block lengths before searching, even under short-circuit branches.
    var matcher = java.util.regex.Pattern.compile("(?i)\\bORDINAL\\s*\\(\\s*(\\d+)\\s*\\)").matcher(clause == null ? "" : clause);
    while (matcher.find()) {
      int size = Integer.parseInt(matcher.group(1));
      if (size < 2 || steps % size != 0) throw new IllegalArgumentException("ORDINAL block size must divide " + steps + " steps.");
    }
    return RhythmClauseParser.parse(clause);
  }
  public static Search complete(RhythmMask mask, Predicate<BinaryNatural> predicate, int limit, int budget, long seed) {
    if (limit < 1 || limit > 10000 || budget < 1 || budget > 100000)
      throw new IllegalArgumentException("Use 1–10,000 results and 1–100,000 attempts.");
    Objects.requireNonNull(predicate);
    check();
    int[] unknown = java.util.stream.IntStream.range(0, mask.bits().length()).filter(i -> mask.bits().charAt(i) == '?').toArray();
    BigInteger count = BigInteger.ONE.shiftLeft(unknown.length), modulusMask = count.subtract(BigInteger.ONE);
    Random random = new Random(seed);
    BigInteger value = new BigInteger(unknown.length, random);
    BigInteger stride = new BigInteger(unknown.length, random).setBit(0);
    // An odd affine stride visits every assignment exactly once modulo 2^unknowns.
    int attempts = 0;
    List<String> results = new ArrayList<>();
    while (attempts < budget && BigInteger.valueOf(attempts).compareTo(count) < 0 && results.size() < limit) {
      check();
      char[] bits = mask.bits().toCharArray();
      for (int i = 0; i < unknown.length; i++) bits[unknown[i]] = value.testBit(i) ? '1' : '0';
      String candidate = new String(bits);
      attempts++;
      if (predicate.test(new RhythmMask(candidate).rhythm())) results.add(candidate);
      value = value.add(stride).and(modulusMask);
    }
    Stop stop = BigInteger.valueOf(attempts).equals(count) ? Stop.EXHAUSTED : results.size() == limit ? Stop.RESULT_LIMIT : Stop.BUDGET;
    return new Search(results, attempts, stop);
  }
  public static int hamming(String a, String b) {
    if (a.length() != b.length()) throw new IllegalArgumentException("Rhythm lengths must agree.");
    int d = 0;
    for (int i = 0; i < a.length(); i++) if (a.charAt(i) != b.charAt(i)) d++;
    return d;
  }
  /** Simple explicit meter: weak onset whose following beat boundary is silent. Not a perceptual model. */
  public static double syncopation(String bits, int beatSteps) {
    if (beatSteps < 1 || bits.length() % beatSteps != 0) throw new IllegalArgumentException("Steps per beat must divide the rhythm length.");
    int hits = 0, displaced = 0;
    for (int i = 0; i < bits.length(); i++) if (bits.charAt(i) == '1') {
      hits++;
      if (i % beatSteps != 0 && bits.charAt(((i / beatSteps + 1) * beatSteps) % bits.length()) == '0') displaced++;
    }
    return hits == 0 ? 0 : (double) displaced / hits;
  }
  public record Selection(int targetChanges, int targetHits, double temperature, int memory,
      double euclideanWeight, double targetSyncopation, int beatSteps) {
    public Selection {
      if (targetChanges < 0 || targetHits < -1 || !Double.isFinite(temperature) || temperature <= 0
          || memory < 0 || memory > 1000 || !Double.isFinite(euclideanWeight) || euclideanWeight < 0 || euclideanWeight > 100
          || !Double.isFinite(targetSyncopation) || (targetSyncopation != -1 && (targetSyncopation < 0 || targetSyncopation > 1)) || beatSteps < 1)
        throw new IllegalArgumentException("Invalid selection settings (temperature > 0; sync target -1 or 0..1).");
    }
    public void validateLength(int n) {
      if (targetChanges > n || targetHits > n || n % beatSteps != 0) throw new IllegalArgumentException("Targets must fit the rhythm; steps per beat must divide its length.");
    }
  }
  public record Score(double change, double density, double euclidean, double repetition, double syncopation) {
    public double total() { return change + density + euclidean + repetition + syncopation; }
  }
  public static Score score(String current, String candidate, List<String> history, Selection options) {
    int n = current.length();
    options.validateLength(n);
    var rhythm = new RhythmMask(candidate).rhythm();
    double repeat = 0;
    for (int i = Math.max(0, history.size() - options.memory()); i < history.size(); i++)
      repeat = Math.max(repeat, 1.0 - (double) hamming(history.get(i), candidate) / n);
    return new Score((double)Math.abs(hamming(current, candidate) - options.targetChanges()) / n,
        options.targetHits() < 0 ? 0 : (double)Math.abs(rhythm.getK() - options.targetHits()) / n,
        options.euclideanWeight() == 0 ? 0 : options.euclideanWeight() * Euclidean.distance(rhythm) / n,
        repeat,
        options.targetSyncopation() < 0 ? 0 : Math.abs(syncopation(candidate, options.beatSteps()) - options.targetSyncopation()));
  }
  public record Choice(String bits, Score score, double probability) {}
  public static Choice choose(String current, List<String> candidates, List<String> history, Selection options, Random random) {
    if (candidates.isEmpty()) throw new IllegalArgumentException("No candidates to select.");
    List<Score> scores = new ArrayList<>();
    double min = Double.POSITIVE_INFINITY;
    for (String candidate : candidates) { check(); Score s = score(current, candidate, history, options); scores.add(s); min = Math.min(min, s.total()); }
    double[] weights = new double[scores.size()];
    double sum = 0;
    for (int i = 0; i < weights.length; i++) { weights[i] = Math.exp(-(scores.get(i).total() - min) / options.temperature()); sum += weights[i]; }
    double draw = random.nextDouble() * sum;
    int selected = weights.length - 1;
    for (int i = 0; i < weights.length; i++) { draw -= weights[i]; if (draw < 0) { selected = i; break; } }
    return new Choice(candidates.get(selected), scores.get(selected), weights[selected] / sum);
  }
  public record Settings(String where, String transition, int eraseCount, int unitWidth, RhythmMask.Erasure erasure,
      Set<Integer> protectedSteps, int limit, int budget, long seed, Selection selection) {
    public Settings {
      protectedSteps = Set.copyOf(protectedSteps);
      Objects.requireNonNull(erasure); Objects.requireNonNull(selection);
      if (eraseCount < 0 || unitWidth < 1 || limit < 1 || limit > 10000 || budget < 1 || budget > 100000)
        throw new IllegalArgumentException("Invalid erasure/search limits.");
    }
  }
  public record Step(long iteration, long seed, String previous, String mask, String bits, int changed,
      int hits, Score score, double probability, int candidates, int attempts, Stop stop, boolean held) {}

  /** Sequential state is owned by one worker. Same initial rhythm/settings produce the same stream. */
  public static final class Session {
    private String current;
    private final Settings settings;
    private final Predicate<BinaryNatural> predicate, transition;
    private final List<String> history = new ArrayList<>();
    private long iteration;
    public Session(String initial, Settings settings) {
      RhythmMask mask = new RhythmMask(initial);
      var rhythm = mask.rhythm();
      this.settings = settings;
      settings.selection().validateLength(initial.length());
      predicate = rule(settings.where(), initial.length());
      transition = rule(settings.transition(), initial.length() * 2);
      if (!predicate.test(rhythm)) throw new IllegalArgumentException("Starting rhythm does not satisfy the rhythm clause.");
      mask.erase(settings.eraseCount(), settings.unitWidth(), settings.erasure(), settings.protectedSteps(), new Random(settings.seed()));
      current = initial;
      history.add(initial);
    }
    public Step next() {
      check();
      long stepSeed = settings.seed() + iteration;
      Random random = new Random(stepSeed);
      String previous = current;
      RhythmMask mask = new RhythmMask(current).erase(settings.eraseCount(), settings.unitWidth(), settings.erasure(), settings.protectedSteps(), random);
      Search result = complete(mask, r -> predicate.test(r) && transition.test(new BinaryNatural(
          new BigInteger(previous, 2).shiftLeft(previous.length()).or(toInteger(r)), previous.length() * 2)), settings.limit(), settings.budget(), random.nextLong());
      // Preserve the current rhythm as a fallback, but never override a hard transition rule.
      boolean currentAllowed = transition.test(new BinaryNatural(new BigInteger(previous + previous, 2), previous.length() * 2));
      List<String> pool = new ArrayList<>(result.candidates());
      if (currentAllowed && !pool.contains(current)) pool.add(current);
      if (pool.isEmpty()) throw new IllegalStateException("No valid transition found (" + result.stop() + "). Increase the budget, release more steps, or change the rules.");
      Choice choice = choose(current, pool, history, settings.selection(), random);
      current = choice.bits();
      history.add(current);
      while (history.size() > Math.max(1, settings.selection().memory())) history.remove(0);
      return new Step(iteration++, stepSeed, previous, mask.bits(), current, hamming(previous, current),
          new RhythmMask(current).rhythm().getK(), choice.score(), choice.probability(), pool.size(), result.attempts(), result.stop(), current.equals(previous));
    }
  }
  private static BigInteger toInteger(BinaryNatural r) {
    BigInteger value = BigInteger.ZERO;
    for (int i = r.nextSetBit(0); i >= 0; i = r.nextSetBit(i + 1)) value = value.setBit(i);
    return value;
  }
}
