package name.ncg777.maths.numbers.fixed.rhythm;

import java.util.BitSet;

import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.sequences.Sequence;

/**
 * A normalised distance measure between two {@link BinaryNatural} rhythm patterns.
 *
 * <p>All returned values lie in {@code [0, 1]}, where 0 means identical and 1 means maximally
 * distant.  The distance is assumed to be bidirectional (symmetric).
 *
 * <p>Built-in implementations are available as static factory methods:
 * <ul>
 *   <li>{@link #hamming()} – proportion of differing bits (XOR-cardinality / length)</li>
 *   <li>{@link #jaccard()} – 1 − Jaccard similarity</li>
 *   <li>{@link #intervalVector()} – normalised L1 distance between interval-class vectors</li>
 * </ul>
 */
@FunctionalInterface
public interface RhythmDistance {

  /**
   * Computes the distance between two rhythms.
   *
   * @param a first rhythm (the two must share the same length {@code getN()})
   * @param b second rhythm
   * @return a value in {@code [0, 1]}
   */
  double distance(BinaryNatural a, BinaryNatural b);

  // ─────────────────────────────────────────── built-in implementations ──

  /**
   * Normalised Hamming distance: {@code popcount(a XOR b) / length}.
   * This is the fraction of bit positions that differ.
   */
  static RhythmDistance hamming() {
    return (a, b) -> {
      int n = a.getN();
      if (n == 0) return 0.0;
      BitSet xor = (BitSet) a.clone();
      xor.xor(b);
      return (double) xor.cardinality() / n;
    };
  }

  /**
   * Jaccard distance: {@code 1 - |A ∩ B| / |A ∪ B|}.
   * Returns 0 when both patterns are the all-zeros pattern.
   */
  static RhythmDistance jaccard() {
    return (a, b) -> {
      BitSet inter = (BitSet) a.clone();
      inter.and(b);
      BitSet union = (BitSet) a.clone();
      union.or(b);
      int u = union.cardinality();
      if (u == 0) return 0.0;
      return 1.0 - (double) inter.cardinality() / u;
    };
  }

  /**
   * Interval-vector L1 distance, normalised.
   *
   * <p>The interval vector of a rhythm of length {@code n} has {@code n/2} entries.  This
   * measure computes the L1 (Manhattan) norm of the difference between the two vectors and
   * normalises so that the maximum possible value maps to 1.
   */
  static RhythmDistance intervalVector() {
    return (a, b) -> {
      Sequence va = a.getIntervalVector();
      Sequence vb = b.getIntervalVector();
      if (va.size() == 0) return 0.0;
      int sum = 0;
      int maxDiff = 0;
      for (int i = 0; i < va.size(); i++) {
        int diff = Math.abs(va.get(i) - vb.get(i));
        sum += diff;
        maxDiff = Math.max(maxDiff, Math.max(va.get(i), vb.get(i)));
      }
      // Normalise by the maximum conceivable L1 difference
      double maxPossible = (double) maxDiff * va.size() * 2;
      if (maxPossible == 0) return 0.0;
      return Math.min(1.0, (double) sum / maxPossible);
    };
  }

  /**
   * Parses a distance name string (case-insensitive):
   * {@code HAMMING}, {@code JACCARD}, {@code INTERVAL_VECTOR}.
   */
  static RhythmDistance parse(String name) {
    return switch (name.trim().toUpperCase()) {
      case "HAMMING"         -> hamming();
      case "JACCARD"         -> jaccard();
      case "INTERVAL_VECTOR",
           "INTERVAL"        -> intervalVector();
      default -> throw new IllegalArgumentException("Unknown distance: '" + name + "'");
    };
  }
}
