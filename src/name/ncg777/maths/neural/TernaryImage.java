package name.ncg777.maths.neural;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

/** Exact 8x8 image encoding. Transparency is data, never a missing-value marker. */
public final class TernaryImage {
  public static final int SIDE = 8;
  private TernaryImage() {}

  public static TernaryAssociativeMemory.Cue decode(BufferedImage image) {
    if (image == null || image.getWidth() != SIDE || image.getHeight() != SIDE)
      throw new IllegalArgumentException("Image must be exactly 8 by 8 pixels");
    int[] values = new int[SIDE * SIDE]; boolean[] known = new boolean[values.length];
    Arrays.fill(known, true);
    for (int y = 0; y < SIDE; y++) for (int x = 0; x < SIDE; x++) {
      int argb = image.getRGB(x, y), alpha = argb >>> 24, rgb = argb & 0xffffff;
      if (alpha == 0) values[y * SIDE + x] = 0;
      else if (alpha == 255 && (rgb == 0 || rgb == 0xffffff)) values[y * SIDE + x] = rgb == 0 ? -1 : 1;
      else throw new IllegalArgumentException("Pixels must be fully transparent, opaque black, or opaque white");
    }
    return new TernaryAssociativeMemory.Cue(values, known);
  }

  public static BufferedImage encode(TernaryAssociativeMemory.Cue cue) {
    int[] values = cue.values(); boolean[] known = cue.known();
    if (values.length != SIDE * SIDE) throw new IllegalArgumentException("Image requires 64 positions");
    BufferedImage image = new BufferedImage(SIDE, SIDE, BufferedImage.TYPE_INT_ARGB);
    for (int i = 0; i < values.length; i++) {
      if (!known[i]) throw new IllegalArgumentException("Fill unknown pixels before exporting: transparency means a known zero");
      image.setRGB(i % SIDE, i / SIDE, values[i] == 0 ? 0 : values[i] == 1 ? 0xffffffff : 0xff000000);
    }
    return image;
  }

  public static TernaryAssociativeMemory.Cue read(Path path) throws IOException {
    // Inspect dimensions before decoding, so a large source image cannot allocate a large bitmap.
    try (ImageInputStream input = ImageIO.createImageInputStream(path.toFile())) {
      if (input == null) throw new IOException("Cannot open image");
      var readers = ImageIO.getImageReaders(input);
      if (!readers.hasNext()) throw new IOException("Not a readable PNG image");
      var reader = readers.next();
      try {
        reader.setInput(input);
        if (!reader.getFormatName().equalsIgnoreCase("PNG")) throw new IOException("Choose a PNG image");
        if (reader.getWidth(0) != SIDE || reader.getHeight(0) != SIDE)
          throw new IllegalArgumentException("Image must be exactly 8 by 8 pixels");
        return decode(reader.read(0));
      } finally { reader.dispose(); }
    }
  }

  public static void write(Path path, TernaryAssociativeMemory.Cue cue) throws IOException {
    if (!ImageIO.write(encode(cue), "PNG", path.toFile())) throw new IOException("PNG writer unavailable");
  }
}
