package name.ncg777.maths.neural;

import java.util.*;
import java.util.concurrent.CancellationException;
import name.ncg777.maths.neural.TernaryContours.Raster;

/** Synthetic contour restoration and explicit even/odd enclosure semantics. */
public final class TernarySegmentation {
  private TernarySegmentation() {}
  public enum Shape {
    RECTANGLE("Rectangle"), ELLIPSE("Ellipse"), TWO_SHAPES("Two shapes"), HOLE("Shape with hole"),
    NESTED_ISLAND("Island inside hole"), BORDER_CUT("Cut by image border");
    private final String label;
    Shape(String label) { this.label = label; }
    @Override public String toString() { return label; }
  }

  public record Example(Raster observed, boolean[] boundary, int[] regions) {
    public Example {
      boundary = boundary.clone(); regions = regions.clone();
      if (boundary.length != observed.width() * observed.height() || regions.length != boundary.length)
        throw new IllegalArgumentException("Mismatched labels");
      for (int i = 0; i < regions.length; i++) if (regions[i] < -1 || regions[i] > 1 || boundary[i] != (regions[i] == 0))
        throw new IllegalArgumentException("Expected consistent boundary and region labels");
    }
    @Override public boolean[] boundary() { return boundary.clone(); }
    @Override public int[] regions() { return regions.clone(); }
  }

  /** Truth is constructed before corruption. Deleted runs are observations of empty
   * space, not missing-value masks; the restoration network must infer them. */
  public static Example generate(int side, Shape shape, int gapLength, double noise, long seed) {
    if (side < 24 || side > 128 || gapLength < 0 || gapLength > 8 || !Double.isFinite(noise) || noise < 0 || noise > .1)
      throw new IllegalArgumentException("Use size 24–128, gaps 0–8, and noise 0–10%");
    Objects.requireNonNull(shape); Random random = new Random(seed);
    boolean[] filled = new boolean[side * side];
    int margin = side / 8 + random.nextInt(Math.max(1, side / 16));
    int right = side - margin - 1, bottom = side - margin - 1;
    int mid = side / 2;
    for (int y = 0; y < side; y++) for (int x = 0; x < side; x++) {
      boolean outer = x >= margin && y >= margin && x <= right && y <= bottom;
      boolean hole = x >= side / 3 && x <= side * 2 / 3 && y >= side / 3 && y <= side * 2 / 3;
      boolean island = Math.abs(x - mid) <= Math.max(1, side / 16) && Math.abs(y - mid) <= Math.max(1, side / 16);
      filled[y * side + x] = switch (shape) {
        case RECTANGLE -> outer;
        case ELLIPSE -> Math.pow((x - mid) / (double) (mid - margin), 2) + Math.pow((y - mid) / (double) (mid - margin), 2) <= 1;
        case TWO_SHAPES -> outer && (x < mid - 2 || x > mid + 2);
        case HOLE -> outer && !hole;
        case NESTED_ISLAND -> outer && (!hole || island);
        case BORDER_CUT -> x <= right && y >= margin && y <= bottom;
      };
    }
    // Border-cut means the shape continues outside the crop. Do not invent a
    // closing contour along the cut; this deliberately challenges exterior seeding.
    boolean[] boundary = new boolean[filled.length];
    for (int y = 0; y < side; y++) for (int x = 0; x < side; x++) if (filled[y * side + x]) {
      for (int[] d : FOUR) {
        int nx = x + d[0], ny = y + d[1];
        if (nx < 0 && shape == Shape.BORDER_CUT) continue;
        if (nx < 0 || ny < 0 || nx >= side || ny >= side || !filled[ny * side + nx]) boundary[y * side + x] = true;
      }
    }
    int[] regions = new int[filled.length], observed = new int[filled.length];
    List<Integer> edges = new ArrayList<>();
    for (int i = 0; i < filled.length; i++) {
      regions[i] = boundary[i] ? 0 : filled[i] ? 1 : -1;
      observed[i] = boundary[i] ? 1 : 0; if (boundary[i]) edges.add(i);
    }
    if (gapLength > 0 && !edges.isEmpty()) {
      // Remove a connected run from each of several randomly chosen contour locations.
      for (int run = 0; run < Math.max(1, side / 24); run++) {
        int current = edges.get(random.nextInt(edges.size())); Set<Integer> removed = new HashSet<>();
        for (int k = 0; k < gapLength; k++) {
          observed[current] = 0; removed.add(current); List<Integer> next = new ArrayList<>();
          int x = current % side, y = current / side;
          for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) {
            int nx = x + dx, ny = y + dy;
            if (nx >= 0 && ny >= 0 && nx < side && ny < side) {
              int j = ny * side + nx; if (boundary[j] && !removed.contains(j)) next.add(j);
            }
          }
          if (next.isEmpty()) break; current = next.get(random.nextInt(next.size()));
        }
      }
    }
    for (int i = 0; i < observed.length; i++) if (random.nextDouble() < noise) observed[i] = 1 - observed[i];
    return new Example(new Raster(side, side, observed), boundary, regions);
  }

  public record Regions(int width, int height, int[] trits, int ambiguousContours, boolean touchesBorder) {
    public Regions {
      dimensions(width, height, trits.length); trits = trits.clone();
      if (ambiguousContours < 0) throw new IllegalArgumentException("Negative contour count");
      for (int value : trits) if (value < -1 || value > 1) throw new IllegalArgumentException("Expected region trits");
    }
    @Override public int[] trits() { return trits.clone(); }
  }

  private static final int[][] FOUR = {{-1,0},{1,0},{0,-1},{0,1}};

  /** Free space uses four-connectivity, contour bands eight-connectivity. Regions
   * adjoining the frame start outside. Crossing a connected contour band flips
   * inside/outside; two nested bands therefore preserve a hole. Branched/open bands
   * cannot establish a unique enclosure and are reported, not silently certified. */
  public static Regions regions(int width, int height, boolean[] boundary) {
    dimensions(width, height, boundary.length); check();
    int[] ids = new int[boundary.length]; Arrays.fill(ids, -1);
    List<Boolean> band = new ArrayList<>(), border = new ArrayList<>();
    int[] queue = new int[boundary.length];
    for (int start = 0; start < ids.length; start++) if (ids[start] < 0) {
      check(); int id = band.size(), head = 0, tail = 0; boolean isBand = boundary[start], touches = false;
      ids[start] = id; queue[tail++] = start;
      while (head < tail) {
        int i = queue[head++], x = i % width, y = i / width;
        touches |= x == 0 || y == 0 || x == width - 1 || y == height - 1;
        for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) {
          if (dx == 0 && dy == 0 || !isBand && Math.abs(dx) + Math.abs(dy) != 1) continue;
          int nx = x + dx, ny = y + dy;
          if (nx < 0 || ny < 0 || nx >= width || ny >= height) continue;
          int j = ny * width + nx;
          if (ids[j] < 0 && boundary[j] == isBand) { ids[j] = id; queue[tail++] = j; }
        }
      }
      band.add(isBand); border.add(touches);
    }
    List<Set<Integer>> adjacent = new ArrayList<>();
    for (int i = 0; i < band.size(); i++) adjacent.add(new HashSet<>());
    for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
      int i = y * width + x;
      for (int[] d : FOUR) {
        int nx = x + d[0], ny = y + d[1];
        if (nx >= 0 && ny >= 0 && nx < width && ny < height) {
          int other = ids[ny * width + nx]; if (ids[i] != other) adjacent.get(ids[i]).add(other);
        }
      }
    }
    // Bipartite 0–1 BFS: entering a band costs one crossing, leaving costs zero.
    int[] depth = new int[band.size()]; Arrays.fill(depth, Integer.MAX_VALUE);
    ArrayDeque<Integer> pending = new ArrayDeque<>(); boolean touchesBorder = false; int ambiguous = 0;
    for (int i = 0; i < band.size(); i++) {
      if (band.get(i)) { touchesBorder |= border.get(i); if (adjacent.get(i).size() != 2 || border.get(i)) ambiguous++; }
      if (border.get(i)) { depth[i] = band.get(i) ? 1 : 0; if (band.get(i)) pending.addLast(i); else pending.addFirst(i); }
    }
    while (!pending.isEmpty()) {
      check(); int i = pending.removeFirst();
      for (int j : adjacent.get(i)) {
        int cost = band.get(j) ? 1 : 0;
        if (depth[j] > depth[i] + cost) { depth[j] = depth[i] + cost; if (cost == 0) pending.addFirst(j); else pending.addLast(j); }
      }
    }
    int[] result = new int[boundary.length];
    for (int i = 0; i < result.length; i++) result[i] = boundary[i] ? 0 : (depth[ids[i]] % 2 == 1 ? 1 : -1);
    return new Regions(width, height, result, ambiguous, touchesBorder);
  }

  /** Square morphological closing with transparent/empty padding. The expanded
   * dilation avoids accidentally eroding the image border during closing. */
  public static boolean[] close(int width, int height, boolean[] boundary, int radius) {
    dimensions(width, height, boundary.length);
    if (radius < 0 || radius > 2) throw new IllegalArgumentException("Closing radius must be 0, 1 or 2");
    if (radius == 0) { check(); return boundary.clone(); }
    int w = width + 2 * radius, h = height + 2 * radius; boolean[] dilated = new boolean[w * h];
    for (int y = -radius; y < height + radius; y++) {
      check();
      for (int x = -radius; x < width + radius; x++) {
        boolean on = false;
        for (int dy = -radius; dy <= radius; dy++) for (int dx = -radius; dx <= radius; dx++) {
          int nx = x + dx, ny = y + dy;
          if (nx >= 0 && ny >= 0 && nx < width && ny < height && boundary[ny * width + nx]) on = true;
        }
        dilated[(y + radius) * w + x + radius] = on;
      }
    }
    boolean[] result = new boolean[boundary.length];
    for (int y = 0; y < height; y++) {
      check();
      for (int x = 0; x < width; x++) {
        boolean on = true;
        for (int dy = -radius; dy <= radius; dy++) for (int dx = -radius; dx <= radius; dx++)
          on &= dilated[(y + radius + dy) * w + x + radius + dx];
        result[y * width + x] = on;
      }
    }
    return result;
  }

  public record Score(long truePositive, long falsePositive, long falseNegative, long intersection, long union) {
    public double precision() { return truePositive + falsePositive == 0 ? 0 : (double) truePositive / (truePositive + falsePositive); }
    public double recall() { return truePositive + falseNegative == 0 ? 0 : (double) truePositive / (truePositive + falseNegative); }
    public double f1() { long n = 2 * truePositive + falsePositive + falseNegative; return n == 0 ? 1 : 2.0 * truePositive / n; }
    public double iou() { return union == 0 ? 1 : (double) intersection / union; }
    public Score plus(Score b) { return new Score(truePositive + b.truePositive, falsePositive + b.falsePositive, falseNegative + b.falseNegative, intersection + b.intersection, union + b.union); }
  }
  public static Score score(Example truth, Regions predicted) {
    if (truth.observed.width() != predicted.width || truth.observed.height() != predicted.height)
      throw new IllegalArgumentException("Mismatched prediction dimensions");
    long tp = 0, fp = 0, fn = 0, intersection = 0, union = 0;
    for (int i = 0; i < truth.boundary.length; i++) {
      boolean edge = predicted.trits[i] == 0;
      if (edge && truth.boundary[i]) tp++; else if (edge) fp++; else if (truth.boundary[i]) fn++;
      boolean a = truth.regions[i] == 1, b = predicted.trits[i] == 1;
      if (a && b) intersection++; if (a || b) union++;
    }
    return new Score(tp, fp, fn, intersection, union);
  }
  public static boolean[] observedBoundary(Raster image) {
    int[] pixels = image.pixels(); boolean[] boundary = new boolean[pixels.length];
    for (int i = 0; i < pixels.length; i++) boundary[i] = pixels[i] != 0;
    return boundary;
  }
  static void dimensions(int w, int h, int length) {
    if (w < 1 || h < 1 || w > 128 || h > 128 || length != w * h) throw new IllegalArgumentException("Use matching dimensions up to 128×128");
  }
  static void check() { if (Thread.currentThread().isInterrupted()) throw new CancellationException("Cancelled"); }
}
