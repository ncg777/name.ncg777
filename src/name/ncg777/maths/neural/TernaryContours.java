package name.ncg777.maths.neural;

import java.util.*;

/** Synthetic ternary shapes and a deliberately simple, reproducible boundary definition. */
public final class TernaryContours {
  private TernaryContours() {}

  public record Raster(int width, int height, int[] pixels) {
    public Raster {
      if (width < 1 || height < 1 || width > 128 || height > 128 || pixels.length != width * height)
        throw new IllegalArgumentException("Raster must be between 1 and 128 pixels on each side");
      pixels = pixels.clone();
      for (int p : pixels) if (p < -1 || p > 1) throw new IllegalArgumentException("Expected ternary pixels");
    }
    @Override public int[] pixels() { return pixels.clone(); }
    public int at(int x, int y) {
      return x < 0 || y < 0 || x >= width || y >= height ? 0 : pixels[y * width + x];
    }
  }

  /** Marks the opaque side of a four-neighbour transparency or black/white boundary.
   * Outside the image is transparent. No unknown pixels are accepted here. */
  public static boolean[] boundaries(Raster image) {
    boolean[] result = new boolean[image.width * image.height];
    for (int y = 0; y < image.height; y++) for (int x = 0; x < image.width; x++) {
      int p = image.at(x, y);
      result[y * image.width + x] = p != 0 && (p != image.at(x - 1, y) || p != image.at(x + 1, y)
          || p != image.at(x, y - 1) || p != image.at(x, y + 1));
    }
    return result;
  }

  /** Rectangles, ellipses and horizontal/vertical strokes, with overlaps and both colours. */
  public static Raster generate(int side, long seed) {
    if (side < 8 || side > 128) throw new IllegalArgumentException("Use sides from 8 to 128");
    Random random = new Random(seed); int[] pixels = new int[side * side];
    int shapes = 1 + random.nextInt(4);
    for (int s = 0; s < shapes; s++) {
      int x0 = random.nextInt(side - 1), y0 = random.nextInt(side - 1);
      int w = 2 + random.nextInt(side - x0 - 1), h = 2 + random.nextInt(side - y0 - 1);
      int kind = random.nextInt(4), colour = random.nextBoolean() ? 1 : -1;
      if (kind == 2) h = 1;
      if (kind == 3) w = 1;
      for (int y = y0; y < y0 + h; y++) for (int x = x0; x < x0 + w; x++) {
        double dx = (x + .5 - x0 - w / 2.0) / (w / 2.0);
        double dy = (y + .5 - y0 - h / 2.0) / (h / 2.0);
        if (kind != 1 || dx * dx + dy * dy <= 1) pixels[y * side + x] = colour;
      }
    }
    return new Raster(side, side, pixels);
  }

  /** Small aligned contour library, not a claim of translation-invariant memory. */
  public static List<TernaryAssociativeMemory.Cue> memories() {
    List<TernaryAssociativeMemory.Cue> result = new ArrayList<>();
    for (int colour : new int[] {-1, 1}) for (int shape = 0; shape < 4; shape++) {
      int[] pixels = new int[64]; boolean[] known = new boolean[64]; Arrays.fill(known, true);
      for (int y = 1; y < 7; y++) for (int x = 1; x < 7; x++) {
        boolean on = switch (shape) {
          case 0 -> x == 1 || x == 6 || y == 1 || y == 6;
          case 1 -> x == y || x + y == 7;
          case 2 -> x == 3 || y == 3;
          default -> x == 1 || y == 6;
        };
        if (on) pixels[y * 8 + x] = colour;
      }
      result.add(new TernaryAssociativeMemory.Cue(pixels, known));
    }
    return List.copyOf(result);
  }
}
