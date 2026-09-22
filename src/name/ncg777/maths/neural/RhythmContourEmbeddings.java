package name.ncg777.maths.neural;

import java.util.*;
import java.util.function.IntConsumer;
import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.Natural;
import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.numbers.predicates.ShadowContourIsomorphic;
import name.ncg777.maths.sequences.Sequence;

/** Existing hexadecimal rhythm semantics, rendered as small contour drawings. */
public final class RhythmContourEmbeddings {
  private RhythmContourEmbeddings() {}
  public record Example(String hex, String contour, String shadow, String path, int[] pixels, int equivalentDrawings) {
    public Example { TernaryAutoencoder.validate(pixels); pixels = pixels.clone(); }
    @Override public int[] pixels() { return pixels.clone(); }
  }
  public record Catalogue(List<Example> training, List<Example> testing, int acceptedPatterns, int distinctDrawings) {
    public Catalogue { training = List.copyOf(training); testing = List.copyOf(testing); }
    public TernaryEmbeddingExperiment.Dataset dataset() {
      return new TernaryEmbeddingExperiment.Dataset(training.stream().map(Example::pixels).toArray(int[][]::new), testing.stream().map(Example::pixels).toArray(int[][]::new));
    }
  }

  /** Scan the finite 16-step space; group identical raster drawings before splitting.
   * Empty rhythms have no contour and are excluded even though the predicate accepts them. */
  public static Catalogue catalogue(IntConsumer progress) {
    var predicate = new ShadowContourIsomorphic(); Map<String, Example> groups = new LinkedHashMap<>(); int accepted = 0;
    for (int value = 1; value <= 0xffff; value++) {
      TernarySegmentation.check(); String hex = String.format(Locale.ROOT, "%04X", value);
      BinaryNatural rhythm = new Natural(Cipher.Name.Hexadecimal, hex).toBinaryNatural();
      if (predicate.apply(rhythm)) {
        accepted++; Example example = fromRhythm(hex, rhythm); String key = Arrays.toString(example.pixels);
        Example old = groups.get(key);
        groups.put(key, old == null ? example : new Example(old.hex, old.contour, old.shadow, old.path, old.pixels, old.equivalentDrawings + 1));
      }
      if (progress != null && (value & 4095) == 0) progress.accept(value);
    }
    List<Example> examples = new ArrayList<>(groups.values()); Collections.shuffle(examples, new Random(777));
    int count = Math.min(256, examples.size());
    if (count < 2) throw new IllegalStateException("Not enough distinct rhythm contour drawings");
    int trainCount = count * 3 / 4;
    return new Catalogue(examples.subList(0, trainCount), examples.subList(trainCount, count), accepted, groups.size());
  }

  public static Example example(String hex) {
    if (hex == null || !hex.matches("[0-9a-fA-F]{4}")) throw new IllegalArgumentException("Use exactly four hexadecimal digits");
    String normalized = hex.toUpperCase(Locale.ROOT);
    BinaryNatural rhythm = new Natural(Cipher.Name.Hexadecimal, normalized).toBinaryNatural();
    if (rhythm.getK() == 0) throw new IllegalArgumentException("The empty rhythm has no contour");
    if (!new ShadowContourIsomorphic().apply(rhythm)) throw new IllegalArgumentException("Rhythm is not shadow-contour-isomorphic");
    return fromRhythm(normalized, rhythm);
  }
  private static Example fromRhythm(String hex, BinaryNatural rhythm) {
    Sequence contour = rhythm.getContour(), shadow = rhythm.getShadowContour();
    // Identical to the 'Contour Sequence' calculation in numbers.fixed.apps.Contours.
    Sequence path = contour.circularHoldNonZero().cyclicalAntidifference(0).asOrdinalsUnipolar().addToEach(-1);
    return new Example(hex, contour.toString(), shadow.toString(), path.toString(), rasterize(path), 1);
  }

  /** Uniform event-index x axis and normalized ordinal-height y axis; no closing
   * segment is appended. High values are drawn at the top. Quantization is lossy. */
  public static int[] rasterize(Sequence path) {
    if (path == null || path.isEmpty() || path.size() > 16) throw new IllegalArgumentException("Expected 1–16 contour points");
    int side = 8;
    int[] result = new int[side * side]; int min = path.getMin(), max = path.getMax();
    int lastX = 0, lastY = 0;
    for (int i = 0; i < path.size(); i++) {
      int x = path.size() == 1 ? (side - 1) / 2 : (int) Math.round(i * (side - 1.0) / (path.size() - 1));
      int y = max == min ? side / 2 : side - 1 - (int) Math.round((path.get(i) - min) * (side - 1.0) / (max - min));
      if (i == 0) result[y * side + x] = 1; else line(result, side, lastX, lastY, x, y);
      lastX = x; lastY = y;
    }
    return result;
  }
  private static void line(int[] pixels, int side, int x, int y, int endX, int endY) {
    int dx = Math.abs(endX - x), dy = -Math.abs(endY - y), sx = x < endX ? 1 : -1, sy = y < endY ? 1 : -1, error = dx + dy;
    while (true) {
      pixels[y * side + x] = 1; if (x == endX && y == endY) break;
      int twice = 2 * error; if (twice >= dy) { error += dy; x += sx; } if (twice <= dx) { error += dx; y += sy; }
    }
  }
}
