package name.ncg777.maths.neural;

import java.util.*;
import name.ncg777.maths.numbers.*;
import name.ncg777.maths.numbers.predicates.ShadowContourIsomorphic;
import name.ncg777.maths.numbers.relations.PredicatedDifferences;

/** Deterministic, greedy projection onto four compatible nonempty SCI bars. */
public final class ValidRhythmDecoder {
  private record Candidate(String hex, BinaryNatural rhythm, double[] samples) {}
  private final List<Candidate> candidates;

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
