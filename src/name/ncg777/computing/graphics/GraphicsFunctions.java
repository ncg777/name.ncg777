package name.ncg777.computing.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
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

  public record Cartesian(double x, double y) implements Comparable<Cartesian> {
    @Override
    public int compareTo(Cartesian other) {
      var o = this.x-other.x;
      if(o != 0.0d) return o<0 ?-1 : 1;
      o = this.y-other.y;
      return o < 0 ? -1 : o > 0 ? 1 : 0;
    }
  };
  public record Polar(double r, double theta) implements Comparable<Polar> {
    @Override
    public int compareTo(Polar other) {
      var o = this.r-other.r;
      if(o != 0.0d) return o<0 ?-1 : 1;
      o = this.theta-other.theta;
      return o < 0 ? -1 : o > 0 ? 1 : 0;
    }
  };
  public record MixedCoordinates(Cartesian cartesian, Polar polar) {};
  
  public static void drawColorField2D(Graphics2D g, Function<MixedCoordinates, Color> color, int width, int height) {
    for (int x = 0; x < width; x++) {
        double xnorm = (x / (double) width - 0.5) * 2.0;
        for (int y = 0; y < height; y++) {
            double ynorm = (y / (double) height - 0.5) * 2.0;

            double r = Math.sqrt(xnorm * xnorm + ynorm * ynorm);
            double th = Math.atan2(ynorm, xnorm); // Fixed normalized coordinate usage

            Color c = color.apply(new MixedCoordinates(
                new Cartesian(xnorm, ynorm),
                new Polar(r, th)
            ));
            
            g.setColor(c);
            g.fillRect(x - 1, y-1, 3, 3);
        }
    }
  }
  
  public static void matrixDisk(
      Graphics2D g, 
      MatrixOfDoubles mat, 
      BiFunction<Cartesian, Double, Color> color, 
      int width, 
      int height, 
      boolean interpolate) {
    int m = mat.rowCount();
    int n = mat.columnCount();

    drawColorField2D(g, (params) -> {
      double x = params.cartesian().x;
      double y = params.cartesian().y;

      // Compute polar indices
      double di = params.polar().r * m;
      double adjustedDi = Math.sqrt(di / m) * m; // Uniform area distribution
      int fi = (int) Math.floor(adjustedDi);

      double dj = (((params.polar().theta) / Math.PI) * 0.5 + 0.5) * n;
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

      return color.apply(new Cartesian(x, y), vo);
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
  // Logistic function to model organic focusing behavior
  public static double logisticFocus(double t, double L, double k, double t0) {
      return L / (1 + Math.exp(-k * (t - t0)));
  }
  /**
   * Simulates a focus blur effect on an image by dynamically adjusting the Gaussian blur
   * based on a logistic function that models organic focusing behavior.
   * 
   * The focus distance evolves smoothly over time, and the blur intensity (represented by 
   * the kernel size of the Gaussian blur) is adjusted according to the focus distance.
   *
   * @param image The image on which the focus blur effect will be applied.
   * @param t The current time or frame index. This determines the current focus distance.
   *          The focus distance evolves according to a logistic function over time.
   * @param L The maximum focus distance (upper bound). This value defines the farthest focus point.
   * @param k The growth rate of the focus distance, controlling how quickly it changes over time.
   * @param t0 The midpoint of the focus transition. This defines when the focus distance is halfway
   *           between its minimum and maximum values.
   * @return A new image with the simulated focus blur effect applied.
   */
  public static Mat simulateFocusBlur(Mat image, double t, double L, double k, double t0) {
      // Get the current focus distance using the logistic function
      double focusDistance = logisticFocus(t, L, k, t0);

      // Normalize focusDistance to a reasonable range for blur effect
      int maxKernelSize = 21; // Maximum kernel size
      int minKernelSize = 3;  // Minimum kernel size

      // Map focusDistance to kernel size
      int kernelSize = (int)(maxKernelSize - (focusDistance * (maxKernelSize - minKernelSize)));

      // Ensure kernel size is odd (required for GaussianBlur)
      if (kernelSize % 2 == 0) {
          kernelSize++;
      }

      // Apply Gaussian Blur with the computed kernel size
      Mat blurredImage = new Mat();
      Imgproc.GaussianBlur(image, blurredImage, new Size(kernelSize, kernelSize), 0);

      return blurredImage;
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
      Function<Double,HomoPair<Double>> _p,
      double from_inclusive, 
      double to_exclusive,
      Function<Double,Cartesian> scale,
      Function<Double,Cartesian> translate,
      Function<Double,Double> width,
      BiFunction<Double,Double,Color> color) {
    
  }
  
  public static void drawParametric2DWithLateralBars(
      Graphics2D g, 
      Function<Double,Cartesian> _p,
      double from_inclusive, 
      double to_exclusive,
      Function<Double,Cartesian> scale,
      Function<Double,Cartesian> translate,
      Function<Double,Double> width,
      BiFunction<Double,Double,Color> color,
      Function<Double,Double> deltaf) {
    drawParametric2D(
        g, 
        _p,
        scale,
        translate,
        (DrawingContext ctx) -> 
          {
            var t = ctx.t();
            var sc = ctx.scale().apply(t);
            double sx = sc.x();
            double sy = sc.y();
            var tr = ctx.translate().apply(t);
            double tx = tr.x();
            double ty = tr.y();
            var point = ctx.p().apply(t);
            double A_x = (sx*point.x())+tx;
            double A_y = (sy*point.y())+ty;
            
            var pointp = ctx.p().apply(t+ctx.delta());
            double B_x = (sx*pointp.x())+tx;
            double B_y = (sy*pointp.y())+ty;
            
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
      deltaf);
  }
  
  public record DrawingContext(
      Graphics2D g, 
      double t, 
      Function<Double,Cartesian> p, 
      Function<Double,Cartesian> scale,
      Function<Double,Cartesian> translate, double delta) {};
      
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Cartesian> p,
      Function<Double,Cartesian> scale,
      Function<Double,Cartesian> translate,
      Consumer<DrawingContext> drawf,
      double from_inclusive, 
      double to_exclusive,
      Function<Double,Double> deltaf) {
    for(double t=from_inclusive; t<to_exclusive; t+=deltaf.apply(t)) {
      var delta = deltaf.apply(t);
      
      
      drawf.accept(new DrawingContext(g, t, p, scale, translate, delta));
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
      Function<Double,Cartesian> p,
      Consumer<DrawingContext> drawf) {
    drawParametric2D(g, p, 
        (t) -> new Cartesian(defaultScaleX,defaultScaleY), 
        (t) -> new Cartesian(defaultTranslateX, defaultTranslateY),
        drawf, 
        defaultFrom, 
        defaultTo,
        (t) -> defaultDelta);
  }
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Cartesian> p,
      Consumer<DrawingContext> drawf,
      double from_inclusive, 
      double to_exclusive) {
    drawParametric2D(g, p, 
        (t) -> new Cartesian(defaultScaleX,defaultScaleY), 
        (t) -> new Cartesian(defaultTranslateX, defaultTranslateY),
        drawf, 
        from_inclusive, to_exclusive, 
        (t) -> defaultDelta);
  }
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Cartesian> p,
      Function<Double,Cartesian> scale, 
      Consumer<DrawingContext> drawf,
      double from_inclusive, 
      double to_exclusive) {
    drawParametric2D(g, p,scale,
        (t) -> new Cartesian(defaultTranslateX, defaultTranslateY), 
        drawf, 
        from_inclusive, to_exclusive, 
        (t) -> defaultDelta);
  }
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Cartesian> p,
      Function<Double,Cartesian> scale,
      Function<Double,Cartesian> translate,
      Consumer<DrawingContext> drawf,
      double from_inclusive, 
      double to_exclusive) {
    drawParametric2D(g, p,scale, 
        translate, 
        drawf, 
        from_inclusive, to_exclusive, 
        (t) -> defaultDelta);
  }
}
