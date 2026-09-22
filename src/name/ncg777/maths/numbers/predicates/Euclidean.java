package name.ncg777.maths.numbers.predicates;

import java.util.function.Predicate;
import name.ncg777.maths.numbers.BinaryNatural;

/** Mechanical-word construction of maximally even rhythms, accepting every rotation.
 * Empty and full rhythms are included; compose with NONEMPTY if silence is unwanted. */
public final class Euclidean implements Predicate<BinaryNatural> {
  private final Integer hits;
  public Euclidean() { this.hits = null; }
  public Euclidean(int hits) {
    if (hits < 0) throw new IllegalArgumentException("EUCLIDEAN requires a nonnegative hit count.");
    this.hits = hits;
  }
  @Override public boolean test(BinaryNatural r) {
    return (hits == null || r.getK() == hits) && distance(r) == 0;
  }
  /** Minimum Hamming distance to the Euclidean necklace with the same length and density. */
  public static int distance(BinaryNatural r) {
    int n = r.getN(), k = r.getK(), best = n;
    if (n == 0) return 0;
    for (int rotation = 0; rotation < n; rotation++) {
      int d = 0;
      for (int i = 0; i < n; i++) {
        int j = (i + rotation) % n;
        boolean onset = ((long)(j + 1) * k / n) != ((long)j * k / n);
        if (r.get(i) != onset) d++;
      }
      best = Math.min(best, d);
      if (best == 0) break;
    }
    return best;
  }
}
