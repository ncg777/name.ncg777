package name.ncg777.maths.neural;

import java.util.*;
import name.ncg777.maths.numbers.*;
import name.ncg777.maths.numbers.predicates.ShadowContourIsomorphic;
import name.ncg777.maths.numbers.relations.PredicatedDifferences;

/** Deterministic, greedy projection onto four compatible nonempty SCI bars. */
public final class ValidRhythmDecoder {
  private record Candidate(String hex, BinaryNatural rhythm, double[] samples) {}
  private final List<Candidate> candidates;

  public record Completions(List<RhythmContourStacks.Stack> stacks, boolean searchLimitReached,
      boolean resultLimitReached, int attempts) {
    public Completions { stacks = List.copyOf(stacks); }
  }

  /** Four masks: four hexadecimal digits or sixteen binary digits, with ? unknowns.
   * Bounded backtracking preserves every known digit and all SCI differences. */
  public Completions complete(List<String> masks, int limit, int budget, long seed) {
    if (masks == null || masks.size() != 4) throw new IllegalArgumentException("Enter four bar masks");
    if (limit < 1 || limit > 10000 || budget < 1 || budget > 100000) throw new IllegalArgumentException("Invalid search limits");
    List<List<Candidate>> pools = new ArrayList<>(); Random random = new Random(seed);
    for (String mask : masks) {
      if (mask == null) throw new IllegalArgumentException("Missing bar mask");
      String pattern = mask.trim().toUpperCase(Locale.ROOT);
      boolean binary = pattern.matches("[01?]{16}");
      if (!binary && !pattern.matches("[0-9A-F?]{4}")) throw new IllegalArgumentException("Each bar needs four hex digits or sixteen 0/1 steps; use ? for unknowns");
      String regex = pattern.replace("?", "."); List<Candidate> pool = new ArrayList<>();
      for (Candidate candidate : candidates) {
        TernarySegmentation.check();
        String value = binary ? String.format(Locale.ROOT, "%16s", Integer.toBinaryString(Integer.parseInt(candidate.hex, 16))).replace(' ', '0') : candidate.hex;
        if (value.matches(regex)) pool.add(candidate);
      }
      Collections.shuffle(pool, random); pools.add(pool);
    }
    Integer[] order = {0,1,2,3}; Arrays.sort(order, Comparator.comparingInt(i -> pools.get(i).size()));
    Search search = new Search(pools, order, limit, budget); search.visit(0);
    return new Completions(search.results, search.exhausted, search.results.size() == limit, search.attempts);
  }

  private static final class Search {
    final List<List<Candidate>> pools; final Integer[] order; final int limit, budget;
    final Candidate[] selected = new Candidate[4]; final List<RhythmContourStacks.Stack> results = new ArrayList<>();
    final PredicatedDifferences difference = new PredicatedDifferences(new ShadowContourIsomorphic());
    int attempts; boolean exhausted;
    Search(List<List<Candidate>> pools, Integer[] order, int limit, int budget) {
      this.pools = pools; this.order = order; this.limit = limit; this.budget = budget;
    }
    void visit(int depth) {
      TernarySegmentation.check();
      if (depth == 4) {
        List<String> hex = new ArrayList<>(); double[] values = new double[64];
        for (int row = 0; row < 4; row++) { hex.add(selected[row].hex); System.arraycopy(selected[row].samples, 0, values, row * 16, 16); }
        results.add(new RhythmContourStacks.Stack(hex, values)); return;
      }
      int row = order[depth];
      for (Candidate candidate : pools.get(row)) {
        TernarySegmentation.check();
        if (results.size() == limit || exhausted) return;
        if (attempts == budget) { exhausted = true; return; } attempts++;
        boolean valid = true;
        for (int previous = 0; previous < depth; previous++) if (!difference.test(candidate.rhythm, selected[order[previous]].rhythm)) { valid = false; break; }
        if (valid) { selected[row] = candidate; visit(depth + 1); }
      }
    }
  }

  public ValidRhythmDecoder() {
    List<Candidate> result = new ArrayList<>();
    var sci = new ShadowContourIsomorphic();
    for (int value = 1; value <= 65535; value++) {
      TernarySegmentation.check();
      String hex = String.format(Locale.ROOT, "%04X", value);
      var rhythm = new Natural(Cipher.Name.Hexadecimal, hex).toBinaryNatural();
      if (sci.apply(rhythm)) result.add(new Candidate(hex, rhythm,
          RhythmContourStacks.samples(RhythmContourEmbeddings.example(hex))));
    }
    candidates = List.copyOf(result);
  }

  /** Minimize each row's squared error given previously selected rows. This
   * guarantees validity, not a globally closest stack or the original rhythms. */
  public RhythmContourStacks.Stack decode(double[] decoded) {
    if (decoded == null || decoded.length != 64) throw new IllegalArgumentException("Expected 64 contour heights");
    for (double v : decoded) if (!Double.isFinite(v) || v < -1 || v > 1) throw new IllegalArgumentException("Invalid contour height");
    var difference = new PredicatedDifferences(new ShadowContourIsomorphic());
    List<Candidate> selected = new ArrayList<>(); List<String> hex = new ArrayList<>(); double[] values = new double[64];
    for (int row = 0; row < 4; row++) {
      Candidate best = null; double bestError = Double.POSITIVE_INFINITY;
      for (Candidate candidate : candidates) {
        TernarySegmentation.check(); double error = 0;
        for (int i = 0; i < 16; i++) { double d = decoded[row * 16 + i] - candidate.samples[i]; error += d * d; }
        if (error >= bestError) continue;
        boolean valid = true;
        for (Candidate previous : selected) if (!difference.test(candidate.rhythm, previous.rhythm)) { valid = false; break; }
        if (valid) { best = candidate; bestError = error; }
      }
      // Repeating any selected bar is feasible: empty differences are SCI.
      if (best == null) throw new IllegalStateException("No valid rhythm found");
      selected.add(best); hex.add(best.hex); System.arraycopy(best.samples, 0, values, row * 16, 16);
    }
    return new RhythmContourStacks.Stack(hex, values);
  }
}
