package name.ncg777.maths.numbers.fixed.rhythm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import name.ncg777.maths.numbers.Cipher;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Enumerates all CNF (conjunctions of literals) predicate clauses meaningful for a given meter.
 *
 * <p>Each clause is a conjunction (AND) of literals.  A literal is either a predicate atom or
 * its negation (NOT atom).  Atoms are:
 * <ul>
 *   <li>All nullary predicates from {@link RhythmPredicateRegistry} (ENTROPIC_DISPERSION alias
 *       suppressed).</li>
 *   <li>{@code MINIMUM_GAP(k)}, {@code MAXIMUM_GAP(k)} for k = 2 … maxGap
 *       (maxGap defaults to bitLength / 2).</li>
 *   <li>{@code ORDINAL(k)} for every divisor k ≥ 2 of the bit-length.</li>
 * </ul>
 *
 * <p>Clauses containing contradicting literals (P AND NOT P) and clauses with no positive
 * literal are suppressed.  Conjunction width is bounded by {@code --max-width} (default 3).
 *
 * <p>Output is one clause per line on stdout — pipe to a file and use
 * {@code rhythmnetwork-explore.sh} to evaluate them.
 *
 * <h3>Example</h3>
 * <pre>
 *   java ... RhythmClauseEnumeratorApp -L 4 -c Hexadecimal --max-width 2
 * </pre>
 */
@Command(
    name = "rhythm-clause-enum",
    mixinStandardHelpOptions = true,
    description = "Enumerate CNF clauses (conjunctions of literals) for a given meter."
)
public final class RhythmClauseEnumeratorApp implements Callable<Integer> {

  @Option(names = {"-L", "--length"}, defaultValue = "4",
      description = "Number of cipher symbols per rhythm (default: 4)")
  private int length;

  @Option(names = {"-c", "--cipher"}, defaultValue = "Hexadecimal",
      description = "Cipher name: Binary, Octal, Hexadecimal (default: Hexadecimal)")
  private Cipher.Name cipher;

  @Option(names = {"--max-width"}, defaultValue = "3",
      description = "Maximum number of literals in a clause (default: 3)")
  private int maxWidth;

  @Option(names = {"--max-gap"}, defaultValue = "-1",
      description = "Upper bound for MINIMUM_GAP / MAXIMUM_GAP parameter "
                  + "(default: bitLength / 2)")
  private int maxGapParam;

  @Option(names = {"--positive-only"},
      description = "Emit only positive clauses (no NOT literals)")
  private boolean positiveOnly;

  // ────────────────────────────────────────────────────────────────────────

  public static void main(String[] args) {
    System.exit(new CommandLine(new RhythmClauseEnumeratorApp()).execute(args));
  }

  @Override
  public Integer call() {
    long t0 = System.nanoTime();
    int bitsPerSymbol = (int) Math.round(Cipher.getCipher(cipher).information());
    int bitLength     = length * bitsPerSymbol;
    if (maxGapParam < 0) maxGapParam = Math.max(2, bitLength / 2);

    System.err.printf("Clause enumerator: L=%d, cipher=%s, bitLength=%d, maxWidth=%d, maxGap=%d%n",
        length, cipher, bitLength, maxWidth, maxGapParam);

    // ── Atom list ─────────────────────────────────────────────────────────
    List<String> atoms = new ArrayList<>();

    // Nullary predicates (skip alias to avoid duplicates)
    for (String name : RhythmPredicateRegistry.names()) {
      if (name.equals("ENTROPIC_DISPERSION")) continue;
      atoms.add(name);
    }

    // MINIMUM_GAP(k): k = 2..maxGapParam
    for (int k = 2; k <= maxGapParam; k++) atoms.add("MINIMUM_GAP(" + k + ")");

    // MAXIMUM_GAP(k): k = 2..maxGapParam
    for (int k = 2; k <= maxGapParam; k++) atoms.add("MAXIMUM_GAP(" + k + ")");

    // ORDINAL(k): k = every divisor of bitLength that is >= 2
    for (int k = 2; k <= bitLength; k++) {
      if (bitLength % k == 0) atoms.add("ORDINAL(" + k + ")");
    }

    System.err.printf("  %d atoms → %d literals%s%n",
        atoms.size(), positiveOnly ? atoms.size() : atoms.size() * 2,
        positiveOnly ? " (positive only)" : "");

    // ── Literal list: int[]{atomIndex, negated}  (negated: 0=positive, 1=negated) ──
    List<int[]> literals = new ArrayList<>();
    for (int i = 0; i < atoms.size(); i++) {
      literals.add(new int[]{ i, 0 });
      if (!positiveOnly) literals.add(new int[]{ i, 1 });
    }

    // ── Estimate total combinations for progress ─────────────────────────
    int n = literals.size();
    long totalCombinations = 0;
    for (int w = 1; w <= maxWidth; w++) {
      totalCombinations += binomial(n, w);
    }
    System.err.printf("  Evaluating up to %,d candidate combinations (width 1..%d)…%n",
        totalCombinations, maxWidth);

    // ── Enumerate all combinations of size 1..maxWidth ───────────────────
    long emitted = 0;
    long evaluated = 0;
    for (int width = 1; width <= maxWidth; width++) {
      long widthStart = System.nanoTime();
      long widthEmitted = 0;
      int[] idx = new int[width];
      for (int i = 0; i < width; i++) idx[i] = i;
      while (true) {
        evaluated++;
        if (isValid(literals, idx)) {
          System.out.println(clauseString(atoms, literals, idx));
          emitted++;
          widthEmitted++;
        }
        // Advance to the next combination in lexicographic order
        int pos = width - 1;
        while (pos >= 0 && idx[pos] == n - width + pos) pos--;
        if (pos < 0) break;
        idx[pos]++;
        for (int i = pos + 1; i < width; i++) idx[i] = idx[i - 1] + 1;
      }
      System.err.printf("  Width %d: %,d clauses emitted (%.0f ms)%n",
          width, widthEmitted, (System.nanoTime() - widthStart) / 1e6);
    }

    long totalMs = (long) ((System.nanoTime() - t0) / 1e6);
    System.err.printf("  Total: %,d clauses emitted from %,d candidates in %,d ms%n",
        emitted, evaluated, totalMs);
    return 0;
  }

  /** Binomial coefficient C(n, k) — safe for moderate values. */
  private static long binomial(int n, int k) {
    if (k > n) return 0;
    if (k == 0 || k == n) return 1;
    if (k > n - k) k = n - k;
    long result = 1;
    for (int i = 0; i < k; i++) {
      result = result * (n - i) / (i + 1);
    }
    return result;
  }

  /**
   * A selection is valid if:
   * <ul>
   *   <li>No atom appears more than once (regardless of sign).</li>
   *   <li>There is at least one positive (non-negated) literal.</li>
   * </ul>
   */
  private boolean isValid(List<int[]> lits, int[] indices) {
    boolean hasPositive = false;
    for (int i = 0; i < indices.length; i++) {
      int[] li = lits.get(indices[i]);
      if (li[1] == 0) hasPositive = true;
      for (int j = i + 1; j < indices.length; j++) {
        if (li[0] == lits.get(indices[j])[0]) return false; // same atom twice
      }
    }
    return hasPositive;
  }

  private String clauseString(List<String> atoms, List<int[]> lits, int[] indices) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < indices.length; i++) {
      if (i > 0) sb.append(" AND ");
      int[] lit = lits.get(indices[i]);
      if (lit[1] == 1) sb.append("NOT ");
      sb.append(atoms.get(lit[0]));
    }
    return sb.toString();
  }
}
