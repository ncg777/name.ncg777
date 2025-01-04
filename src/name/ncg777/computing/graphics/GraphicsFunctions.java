package name.ncg777.computing.graphics;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;

import name.ncg777.computing.structures.HomoPair;
import name.ncg777.computing.structures.Pixel32Bits;
import name.ncg777.maths.Matrix;
import name.ncg777.maths.MatrixOfDoubles;

public class GraphicsFunctions {
  static {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
  }
  
  public static void writeToPNG(BufferedImage image, String path) throws IOException {
      File output = new File(path);
      ImageIO.write(image, "png", output);
  }
  
  public static Matrix<Pixel32Bits> bufferedImageToMatrix(BufferedImage image) {
    int m = image.getHeight();
    int n = image.getWidth();
    
    var o = new Matrix<Pixel32Bits>(m,n);
    var r = image.getData().getDataBuffer();
    for(int i=0;i<m;i++) {
      for(int j=0;j<n;j++) {
        o.set(i,j,new Pixel32Bits(r.getElem(i*n+j)));
      }
    }
    return o;
  }

  public static BufferedImage matrixToBufferedImage(Matrix<Pixel32Bits> matrix) {
    int height = matrix.rowCount();
    int width = matrix.columnCount();
    var o = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
    var b = o.getData().getDataBuffer();
    for(int i=0;i<height;i++) {
      for(int j=0;j<width;j++) {
        b.setElem((i*width+j), matrix.get(i, j).toInteger());
      }
    }
    return o;
  }
  
  public static BufferedImage matToBufferedImage(Mat mat) {
    int height = mat.height();
    int width = mat.width();
    var o = new BufferedImage(width, height,BufferedImage.TYPE_INT_ARGB);
    var b = o.getData().getDataBuffer();
    for(int i=0;i<height;i++) {
      for(int j=0;j<width;j++) {
        // Get the pixel from Mat (BGRA order)
        double[] pixel = mat.get(i, j);

        // Ensure the Mat is of type CV_8UC4 (BGRA)
        if (pixel == null || pixel.length != 4) {
            throw new IllegalArgumentException("The Mat must have 4 channels (BGRA).");
        }

        // Extract BGRA channels (OpenCV stores in BGRA order)
        int blue = (int) pixel[0];  // Blue channel
        int green = (int) pixel[1]; // Green channel
        int red = (int) pixel[2];   // Red channel
        int alpha = (int) pixel[3]; // Alpha channel

        // Combine into an ARGB integer (Alpha, Red, Green, Blue)
        int argb = (alpha << 24) | (red << 16) | (green << 8) | blue;

        b.setElem((i*width+j), argb);
      }
    }
    return o;
  }
  public static Mat bufferedImageToMat(BufferedImage img) {
    int height = img.getHeight();
    int width = img.getWidth();
    var o = new Mat(new Size(width, height), CvType.CV_8UC4);
    var b = img.getData().getDataBuffer();
    for(int i=0;i<height;i++) {
      for(int j=0;j<width;j++) {
        var p = new Pixel32Bits(b.getElem(i*width+j));

        byte[] pixel = {(byte) p.getB(), (byte) p.getG(), (byte) p.getR(), (byte) p.getA()}; // OpenCV stores pixels in BGRA order

        o.put(i, j, pixel);
      }
    }
    return o;
  }
  
  public static void writeAnimation(String filename, Supplier<Enumeration<Mat>> frames, int width, int height, double fps) {
    writeAnimation(filename, frames,width,height,fps,-1);
  }
  
  public static void writeAnimation(String filename, Supplier<Enumeration<Mat>> frames, int width, int height, double fps, int hardLimit) {
    var vw = new VideoWriter();
    vw.open(filename,VideoWriter.fourcc('A', 'V', 'C', '1'),fps, new Size(width, height), true);
    if (!vw.isOpened()) {
      System.err.println("Failed to open the VideoWriter!");
      return;
    }
    System.out.println("Writing: " + filename);
    var e = frames.get();
    int k=0;
    while(e.hasMoreElements()) {
      var frame = e.nextElement();
      vw.write(frame);
      System.out.print("\rWriting frame " + Integer.toString(++k));
      
      if(hardLimit>0 && k>=hardLimit) break;
    }
    
    vw.release();
    System.out.println("\rDone.\n");
  }

  public record MixedCoordinates(HomoPair<Double> cartesian, HomoPair<Double> polar) {};
  
  public static void drawColorField2D(Graphics2D g, Function<MixedCoordinates, Color> color, int width, int height) {
    for (int x = 0; x < width; x++) {
        double xnorm = (x / (double) width - 0.5) * 2.0;
        for (int y = 0; y < height; y++) {
            double ynorm = (y / (double) height - 0.5) * 2.0;

            double r = Math.sqrt(xnorm * xnorm + ynorm * ynorm);
            double th = Math.atan2(ynorm, xnorm); // Fixed normalized coordinate usage

            Color c = color.apply(new MixedCoordinates(
                HomoPair.makeHomoPair(xnorm, ynorm),
                HomoPair.makeHomoPair(r, th)
            ));
            
            g.setColor(c);
            g.fillRect(x - 1, y-1, 3, 3);
        }
    }
  }
  
  public static void matrixDisk(Graphics2D g, MatrixOfDoubles mat, BiFunction<HomoPair<Double>, Double, Color> color, int width, int height, boolean interpolate) {
    int m = mat.rowCount();
    int n = mat.columnCount();

    drawColorField2D(g, (params) -> {
      double x = params.cartesian().getFirst();
      double y = params.cartesian().getSecond();

      // Compute polar indices
      double di = params.polar().getFirst() * m;
      double adjustedDi = Math.sqrt(di / m) * m; // Uniform area distribution
      int fi = (int) Math.floor(adjustedDi);

      double dj = (((params.polar().getSecond()) / Math.PI) * 0.5 + 0.5) * n;
      while (dj < 0.0) dj += n;
      while (dj >= n) dj -= n;

      int fj = wrapIndex((int) Math.floor(dj), n);

      // Interpolation factors
      double phi = adjustedDi - fi;
      double phj = dj - fj;

      // Fetch base value
      double vo = (fi >= 0 && fi < m) ? mat.get(fi, fj) : 0.0;

      if (interpolate) {
        // Neighbor indices for interpolation
        int fip = fi + 1;
        int fjp = wrapIndex(fj + 1, n);

        double v00 = vo;
        double v10 = (fip < m) ? mat.get(fip, fj) : 0.0;
        double v01 = (fi >= 0 && fi < m) ? mat.get(fi, fjp) : 0.0;
        double v11 = (fip < m) ? mat.get(fip, fjp) : 0.0;

        // Bilinear interpolation
        vo = bilinearInterpolation(phi, phj, v00, v10, v01, v11);
      }

      return color.apply(HomoPair.makeHomoPair(x, y), vo);
    }, width, height);
  }
  
  private static int wrapIndex(int index, int max) {
    return (index + max) % max;
  }

  private static double bilinearInterpolation(double phi, double phj, double v00, double v10, double v01, double v11) {
    return v00 * (1 - phi) * (1 - phj)
        + v10 * phi * (1 - phj)
        + v01 * (1 - phi) * phj
        + v11 * phi * phj;
  }
  
  /**
   * Performs bicubic interpolation given a fractional position (x, y) within a grid.
   * The values parameter must be a 4x4 grid of surrounding data points.
   * x and y must be in the range [0, 1].
   *
   * @param x      The fractional horizontal position within the grid [0, 1].
   * @param y      The fractional vertical position within the grid [0, 1].
   * @param values A 4x4 grid of surrounding values.
   * @return The interpolated value.
   */
  private static double bicubicInterpolation(double x, double y, double[][] values) {
      if (x < 0 || x > 1 || y < 0 || y > 1) {
          throw new IllegalArgumentException("x and y must be in the range [0, 1]");
      }
      if (values.length != 4 || values[0].length != 4) {
          throw new IllegalArgumentException("values must be a 4x4 grid");
      }

      // Interpolation result
      double result = 0.0;

      // Perform bicubic interpolation using the cubic basis function
      for (int i = 0; i < 4; i++) {
          for (int j = 0; j < 4; j++) {
              // Contribution of each surrounding point
              double basis = cubicBasis(x - (i - 1)) * cubicBasis(y - (j - 1));
              result += basis * values[i][j];
          }
      }
      return result;
  }

  /**
   * The cubic basis function used for interpolation.
   * Approximates a smooth cubic curve for interpolation.
   *
   * @param t The distance from the grid point.
   * @return The weight for the given distance.
   */
  private static double cubicBasis(double t) {
      t = Math.abs(t);
      if (t <= 1) {
          return 1 - 2 * t * t + t * t * t; // |t| <= 1
      } else if (t < 2) {
          return 4 - 8 * t + 5 * t * t - t * t * t; // 1 < |t| < 2
      }
      return 0; // |t| >= 2
  }
  
  
  public static Function<Double, Color> interpolateARGBColors(List<Color> colors) {
    if (colors == null || colors.size() < 2) {
        throw new IllegalArgumentException("List of colors must contain at least two colors.");
    }

    return (u) -> {
        // Find the two colors to interpolate between
        int numColors = colors.size();
        double scaledU = u * (numColors - 1);
        int lowerIndex = (int) Math.floor(scaledU);
        int upperIndex = (int) Math.ceil(scaledU);

        // Handle case where u is exactly 1, which corresponds to the last color
        if (lowerIndex == upperIndex) {
            return colors.get(lowerIndex);
        }

        // Interpolate between the two colors
        Color color1 = colors.get(lowerIndex);
        Color color2 = colors.get(upperIndex);

        double fraction = scaledU - lowerIndex;

        // Interpolate each ARGB component (alpha, red, green, blue)
        int alpha = (int) (color1.getAlpha() + (color2.getAlpha() - color1.getAlpha()) * fraction);
        int red = (int) (color1.getRed() + (color2.getRed() - color1.getRed()) * fraction);
        int green = (int) (color1.getGreen() + (color2.getGreen() - color1.getGreen()) * fraction);
        int blue = (int) (color1.getBlue() + (color2.getBlue() - color1.getBlue()) * fraction);

        // Ensure values are within the valid range [0, 255]
        alpha = Math.min(255, Math.max(0, alpha));
        red = Math.min(255, Math.max(0, red));
        green = Math.min(255, Math.max(0, green));
        blue = Math.min(255, Math.max(0, blue));

        return new Color(red, green, blue, alpha);
    };
  }
  
  public static void drawParametric2DWithLateralBars(
      Graphics2D g, 
      Function<Double,Double> x, 
      Function<Double,Double> y, 
      double from_inclusive, 
      double to_exclusive,
      Function<Double,Double> scaleX,
      Function<Double,Double> scaleY,
      Function<Double,Double> translateX,
      Function<Double,Double> translateY,
      Function<Double,Double> width,
      BiFunction<Double,Double,Color> color,
      Function<Double,Double> deltaf) {
    drawParametric2D(
        g,
        x,
        y,
        
        (Double t, HomoPair<HomoPair<Double>> p) -> 
          {
            double sx = scaleX.apply(t);
            double sy = scaleY.apply(t);
          
            double A_x = p.getFirst().getFirst();
            double A_y = p.getFirst().getSecond();
            double B_x = p.getSecond().getFirst();
            double B_y = p.getSecond().getSecond();
            
            double x_ccw = A_x - (B_y-A_y);
            double y_ccw = A_y + (B_x-A_x);
            double x_cw = A_x + (B_y-A_y);
            double y_cw = A_y - (B_x-A_x);
            
            double w = width.apply(t);
            double f = w /
                (2.0*Math.sqrt(
                    Math.pow(((B_x-A_x)), 2.0) + 
                    Math.pow(((B_y-A_y)), 2.0)));
            
            x_ccw = A_x+f*sx*(x_ccw-A_x);
            y_ccw = A_y+f*sy*(y_ccw-A_y);
            x_cw = A_x+f*sx*(x_cw-A_x);
            y_cw = A_y+f*sy*(y_cw-A_y);
            
            double dx = x_ccw-A_x;
            double dy = y_ccw-A_y;
            double l = Math.sqrt(Math.pow(dx, 2.0)+Math.pow(dy, 2.0));
            double invl = 1.0/l;
            
            double interX = 0.0;
            double interY = 0.0;
            // For the clockwise side (from the center to x_cw, y_cw)
            for (double u = 0; u <= 1; u += invl) { // Can adjust step size for finer control
                Color c = color.apply(t, u); // Use the gradient function
                g.setColor(c);
                g.setPaint(c);
                interX = A_x + u * (x_cw - A_x);
                interY = A_y + u * (y_cw - A_y);
                g.fill(new Ellipse2D.Double(interX-1, interY-1, 3, 3));
                interX = A_x + u * (x_ccw - A_x);
                interY = A_y + u * (y_ccw - A_y);
                g.fill(new Ellipse2D.Double(interX-1, interY-1, 3, 3)); 
            }
        },
      from_inclusive,
      to_exclusive,
      scaleX,
      scaleY,
      translateX,
      translateY,
      deltaf);
  }
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Double> x, 
      Function<Double,Double> y,
      BiConsumer<Double, HomoPair<HomoPair<Double>>> drawf,
      double from_inclusive, 
      double to_exclusive,
      Function<Double,Double> scaleX,
      Function<Double,Double> scaleY,
      Function<Double,Double> translateX,
      Function<Double,Double> translateY,
      Function<Double,Double> deltaf) {
    g.setStroke(new BasicStroke(2.0f));
    
    for(double t=from_inclusive; t<to_exclusive; t+=deltaf.apply(t)) {
      var delta = deltaf.apply(t);

      double sx = scaleX.apply(t);
      double sy = scaleY.apply(t);
      double tx = translateX.apply(t);
      double ty = translateY.apply(t);
      
      double A_x = (sx*x.apply(t))+tx;
      double A_y = (sy*y.apply(t))+ty;
      double B_x = (sx*x.apply(t+delta))+tx;
      double B_y = (sy*y.apply(t+delta))+ty;
      
      drawf.accept(t, HomoPair.makeHomoPair(HomoPair.makeHomoPair(A_x, A_y), HomoPair.makeHomoPair(B_x, B_y)));
    }
  }
  
  private static double defaultFrom = 0.0;
  private static double defaultTo = 1.0;
  private static double defaultTranslateX = 0.0;
  private static double defaultTranslateY = 0.0;
  private static double defaultScaleX = 1.0;
  private static double defaultScaleY = 1.0;
  private static double defaultDelta = 0.000005;
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Double> x, 
      Function<Double,Double> y,
      BiConsumer<Double, HomoPair<HomoPair<Double>>> drawf) {
    drawParametric2D(g, x, y, drawf, 
        defaultFrom, 
        defaultTo,  
        (t) -> defaultScaleX, 
        (t) -> defaultScaleY,
        (t) -> defaultTranslateX,
        (t) -> defaultTranslateY,
        (t) -> defaultDelta);
  }
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Double> x, 
      Function<Double,Double> y,
      BiConsumer<Double, HomoPair<HomoPair<Double>>> drawf,
      double from_inclusive, 
      double to_exclusive) {
    drawParametric2D(g, x, y, drawf, from_inclusive, to_exclusive, 
        (t) -> defaultScaleX, 
        (t) -> defaultScaleY,
        (t) -> defaultTranslateX,
        (t) -> defaultTranslateY, 
        (t) -> defaultDelta);
  }
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Double> x, 
      Function<Double,Double> y, 
      BiConsumer<Double, HomoPair<HomoPair<Double>>> drawf,
      double from_inclusive, 
      double to_exclusive,
      Function<Double,Double> scaleX,
      Function<Double,Double> scaleY) {
    drawParametric2D(g, x, y,drawf, from_inclusive, to_exclusive, 
        scaleX,
        scaleY, 
        (t) -> defaultTranslateX, 
        (t) -> defaultTranslateY,
        (t) -> defaultDelta);
  }
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Double> x, 
      Function<Double,Double> y,
      BiConsumer<Double, HomoPair<HomoPair<Double>>> drawf,
      double from_inclusive, 
      double to_exclusive,
      Function<Double,Double> scaleX,
      Function<Double,Double> scaleY,
      Function<Double,Double> translateX,
      Function<Double,Double> translateY) {
    drawParametric2D(g, x, y,drawf, from_inclusive, to_exclusive, 
        scaleX, 
        scaleY, 
        translateX,
        translateY, 
        (t) -> defaultDelta);
  }
}
