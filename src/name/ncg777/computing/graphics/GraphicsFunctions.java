package name.ncg777.computing.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
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
    vw.open(filename,VideoWriter.fourcc('H', '2', '6', '4'),fps, new Size(width, height), true);
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
      frame.release();
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
  
  /**
   * Converts Polar coordinates to Cartesian coordinates.
   *
   * @param polar The Polar coordinate object (r, theta).
   * @return A new Cartesian coordinate object (x, y).
   */
  public static Cartesian toCartesian(Polar polar) {
    // x = r * cos(theta)
    double x = polar.r() * Math.cos(polar.theta());
    // y = r * sin(theta)
    double y = polar.r() * Math.sin(polar.theta());
    return new Cartesian(x, y);
  }

  /**
   * Converts Cartesian coordinates to Polar coordinates.
   *
   * The radius 'r' is calculated as the square root of (x^2 + y^2).
   * The angle 'theta' is calculated using Math.atan2(y, x), which correctly
   * handles all four quadrants and special cases like x=0.
   *
   * @param cartesian The Cartesian coordinate object (x, y).
   * @return A new Polar coordinate object (r, theta).
   */
  public static Polar toPolar(Cartesian cartesian) {
    // r = sqrt(x^2 + y^2)
    double r = Math.sqrt(Math.pow(cartesian.x(), 2) + Math.pow(cartesian.y(), 2));
    // theta = atan2(y, x)
    double theta = Math.atan2(cartesian.y(), cartesian.x());
    return new Polar(r, theta);
  }
  
  public record MixedCoordinates(Cartesian cartesian, Polar polar) {};
  
  public static void drawColorField2D(Graphics2D g, Function<MixedCoordinates, Color> color, int width, int height) {
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Map<HomoPair<Integer>, Color> map = new TreeMap<HomoPair<Integer>, Color>();
    IntStream.range(0, width*height).parallel().forEach(i -> {
      int x = i % width;
      int y = i / width;
      double xnorm = (x / (double) width - 0.5) * 2.0;
      double ynorm = (y / (double) height - 0.5) * 2.0;
      double r = Math.sqrt(xnorm * xnorm + ynorm * ynorm);
      double th = Math.atan2(ynorm, xnorm);
      synchronized(map) {
        map.put(HomoPair.makeHomoPair(x, y), color.apply(new MixedCoordinates(
            new Cartesian(xnorm, ynorm),
            new Polar(r, th)
        )));
      }
    });
    
    for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          g.setColor(map.get(HomoPair.makeHomoPair(x, y)));
          g.fill(new Ellipse2D.Double(x, y, 1, 1));            
        }
    }
  }
  
  public static void matrixDisk(
      Graphics2D g, 
      double[][] mat, 
      BiFunction<MixedCoordinates, Double, Color> color, 
      int width, 
      int height, 
      boolean interpolate) {
    int m = mat.length;
    int n = mat[0].length;

    drawColorField2D(g, (params) -> {
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
      double vo = (fi >= 0 && fi < m) ? mat[fi][fj] : 0.0;

      if (interpolate) {
        // Neighbor indices for interpolation
        int fip = fi + 1;
        int fjp = wrapIndex(fj + 1, n);

        double v00 = vo;
        double v10 = (fip < m) ? mat[fip][fj] : 0.0;
        double v01 = (fi >= 0 && fi < m) ? mat[fi][fjp] : 0.0;
        double v11 = (fip < m) ? mat[fip][fjp] : 0.0;

        // Bilinear interpolation
        vo = bilinearInterpolation(phi, phj, v00, v10, v01, v11);
      }

      return color.apply(params, vo);
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
  
  /**
   * Projects a 3D point onto a specified plane along the ray from the camera and returns the
   * local 2D coordinates on the plane as well as the distance from the camera to the projection point.
   *
   * @param point       The 3D point to project.
   * @param camera      The camera position.
   * @param planeNormal The normal vector of the projection plane (should be normalized).
   * @param planePoint  A point on the projection plane.
   * @return            A double array where:
   *                    - index 0 is the u-coordinate on the plane,
   *                    - index 1 is the v-coordinate on the plane, and
   *                    - index 2 is the distance from the camera to the projection (intersection) point.
   */
  public static double[] perspectiveProjection(Vector3D point, Vector3D camera, 
                                                             Vector3D planeNormal, Vector3D planePoint) {
      // Compute the vector representing the ray from the camera to the point.
      Vector3D rayDir = point.subtract(camera);

      // Calculate the scalar t at which the ray intersects the plane.
      double denominator = rayDir.dotProduct(planeNormal);
      if (Math.abs(denominator) < 1e-6) {
          throw new IllegalArgumentException("The ray is parallel to the plane; cannot compute perspective projection.");
      }
      
      double t = (planePoint.subtract(camera)).dotProduct(planeNormal) / denominator;

      // Compute the intersection point between the ray and the plane.
      Vector3D intersection = camera.add(rayDir.scalarMultiply(t));

      // Create an orthonormal basis for the plane.
      // Choose an arbitrary vector that is not parallel to the plane normal.
      Vector3D arbitrary = Math.abs(planeNormal.dotProduct(new Vector3D(0, 0, 1))) > 0.9 
                                ? new Vector3D(0, 1, 0) 
                                : new Vector3D(0, 0, 1);
                                
      // u is perpendicular to the plane normal.
      Vector3D u = planeNormal.crossProduct(arbitrary).normalize();
      // v is perpendicular to both planeNormal and u.
      Vector3D v = planeNormal.crossProduct(u).normalize();
      
      // Determine the local 2D coordinates by projecting the intersection onto u and v.
      Vector3D diff = intersection.subtract(planePoint);
      double uCoord = diff.dotProduct(u);
      double vCoord = diff.dotProduct(v);
      
      // Compute the physical distance from the camera to the intersection point.
      double distance = intersection.distance(camera);
      
      return new double[]{ uCoord, vCoord, distance };
  }
  
  /**
   * Computes a camera perspective projection of a 3D point.
   * This function projects the given 3D point onto a view plane
   * defined by the target position (which acts as the center of the image plane)
   * and oriented according to the camera's forward, right, and up vectors.
   * 
   * @param point   The 3D point to project.
   * @param camera  The camera position.
   * @param target  The target point the camera is looking at. This point lies on the
   *                image (projection) plane.
   * @param up      The camera's up vector.
   * @return A double array where:
   *         - index 0 is the x-coordinate in the camera's image plane (right direction),
   *         - index 1 is the y-coordinate in the camera's image plane (up direction),
   *         - index 2 is the distance from the camera to the projected point.
   */
  public static double[] cameraPerspectiveProjection(Vector3D point, Vector3D camera, 
                                                                   Vector3D target, Vector3D up) {
      // Calculate the camera's forward, right, and true-up vectors.
      Vector3D forward = target.subtract(camera).normalize();
      Vector3D right = forward.crossProduct(up).normalize();
      Vector3D trueUp = right.crossProduct(forward).normalize();

      // Define the image plane as the plane passing through the target with normal equal to the forward vector.
      Vector3D planePoint = target;
      Vector3D planeNormal = forward;
      
      // Compute the ray from the camera to the point.
      Vector3D rayDir = point.subtract(camera);
      
      // Find t such that camera + rayDir * t lies on the image plane.
      double denominator = rayDir.dotProduct(planeNormal);
      if (Math.abs(denominator) < 1e-6) {
          throw new IllegalArgumentException("The ray is parallel to the image plane; cannot compute projection.");
      }
      double t = (planePoint.subtract(camera)).dotProduct(planeNormal) / denominator;
      
      // Compute the intersection of the ray with the image plane.
      Vector3D intersection = camera.add(rayDir.scalarMultiply(t));
      
      // Compute local coordinates within the image plane.
      Vector3D diff = intersection.subtract(planePoint);
      double xCoord = diff.dotProduct(right);
      double yCoord = diff.dotProduct(trueUp);
      
      // Compute the distance from the camera to the intersection.
      double distance = intersection.distance(camera);
      
      return new double[]{ xCoord, yCoord, distance };
  }
  
  /**
   * Determines whether a projected 2D point lies within the view boundaries.
   *
   * @param projection A double array with:
   *                   - index 0: the x-coordinate,
   *                   - index 1: the y-coordinate,
   *                   - index 2: the distance from the camera.
   * @param minX The minimum x-bound (left edge).
   * @param maxX The maximum x-bound (right edge).
   * @param minY The minimum y-bound (top edge).
   * @param maxY The maximum y-bound (bottom edge).
   * @return true if the x and y coordinates are within the bounds; false otherwise.
   */
  public static boolean isInView(double[] projection, double minX, double maxX, double minY, double maxY) {
      double x = projection[0];
      double y = projection[1];
      return (x >= minX && x <= maxX && y >= minY && y <= maxY);
  }
  
  /**
   * Cycles hue, boosts saturation/value over time (phase in [0,1)), returns BGRA.
   * - src must be CV_8UC4 (BGRA).
   * - hueCyclesPerLoop: number of full hue rotations per full phase loop.
   * - satBase/valBase: multiplicative base (1.0 keeps original), satAmp/valAmp: +/- modulation.
   */
  public static Mat applyPsychedelicHSV(Mat srcBGRA,
                                        double hueCyclesPerLoop,
                                        double phase,
                                        double satBase, double satAmp,
                                        double valBase, double valAmp) {
    if (srcBGRA.type() != CvType.CV_8UC4) {
      throw new IllegalArgumentException("applyPsychedelicHSV expects CV_8UC4 (BGRA) input");
    }

    Mat bgr = new Mat();
    Imgproc.cvtColor(srcBGRA, bgr, Imgproc.COLOR_BGRA2BGR);

    Mat hsv = new Mat();
    Imgproc.cvtColor(bgr, hsv, Imgproc.COLOR_BGR2HSV);

    // Build per-frame parameters
    int hueShift = (int) Math.round(((phase * hueCyclesPerLoop) % 1.0) * 180.0); // OpenCV H in [0,180)
    double sFactor = satBase + satAmp * Math.sin(2.0 * Math.PI * phase);
    double vFactor = valBase + valAmp * Math.cos(2.0 * Math.PI * phase);

    byte[] data = new byte[(int) (hsv.total() * hsv.channels())];
    hsv.get(0, 0, data);

    for (int i = 0; i < data.length; i += 3) {
      int H = data[i] & 0xFF;
      int S = data[i + 1] & 0xFF;
      int V = data[i + 2] & 0xFF;

      // Hue shift modulo 180
      int Hn = (H + hueShift) % 180;

      // Saturation/value scaling
      int Sn = (int) Math.round(Math.max(0.0, Math.min(255.0, S * sFactor)));
      int Vn = (int) Math.round(Math.max(0.0, Math.min(255.0, V * vFactor)));

      data[i] = (byte) Hn;
      data[i + 1] = (byte) Sn;
      data[i + 2] = (byte) Vn;
    }

    hsv.put(0, 0, data);

    Mat bgrOut = new Mat();
    Imgproc.cvtColor(hsv, bgrOut, Imgproc.COLOR_HSV2BGR);

    // Restore alpha
    List<Mat> bgraChannels = new java.util.ArrayList<>(4);
    Core.split(srcBGRA, bgraChannels); // BGRA
    Mat alpha = bgraChannels.get(3);

    Mat dstBGRA = new Mat();
    List<Mat> bgrChannels = new java.util.ArrayList<>(3);
    Core.split(bgrOut, bgrChannels); // B,G,R
    bgrChannels.add(alpha); // append original alpha
    Core.merge(bgrChannels, dstBGRA); // BGRA

    // Cleanup
    bgr.release();
    hsv.release();
    bgrOut.release();

    return dstBGRA;
  }

  /**
   * Kaleidoscope effect by folding angles into N mirrored segments around the center.
   * - src must be CV_8UC4 (BGRA). Output is CV_8UC4.
   * - segments >= 2
   * - angleOffset: radians, rotates the kaleidoscope
   */
  public static Mat applyKaleidoscope(Mat srcBGRA, int segments, double angleOffset) {
    if (srcBGRA.type() != CvType.CV_8UC4) {
      throw new IllegalArgumentException("applyKaleidoscope expects CV_8UC4 (BGRA) input");
    }
    if (segments < 2) {
      throw new IllegalArgumentException("segments must be >= 2");
    }

    int rows = srcBGRA.rows();
    int cols = srcBGRA.cols();
    float cx = (cols - 1) / 2.0f;
    float cy = (rows - 1) / 2.0f;

    Mat mapX = new Mat(rows, cols, CvType.CV_32FC1);
    Mat mapY = new Mat(rows, cols, CvType.CV_32FC1);
    float[] mapXData = new float[rows * cols];
    float[] mapYData = new float[rows * cols];

    final double twoPi = Math.PI * 2.0;
    final double segWidth = twoPi / segments;

    int idx = 0;
    for (int y = 0; y < rows; y++) {
      float dy = y - cy;
      for (int x = 0; x < cols; x++) {
        float dx = x - cx;

        double r = Math.hypot(dx, dy);
        double theta = Math.atan2(dy, dx) - angleOffset;
        // Normalize to [0, 2π)
        theta = theta % twoPi;
        if (theta < 0) theta += twoPi;

        int seg = (int) Math.floor(theta / segWidth);
        double local = theta - seg * segWidth;
        if ((seg & 1) == 1) {
          local = segWidth - local; // mirror odd segments
        }

        double finalAngle = local; // folded into base wedge
        float sx = (float) (cx + r * Math.cos(finalAngle + angleOffset));
        float sy = (float) (cy + r * Math.sin(finalAngle + angleOffset));

        mapXData[idx] = sx;
        mapYData[idx] = sy;
        idx++;
      }
    }

    mapX.put(0, 0, mapXData);
    mapY.put(0, 0, mapYData);

    Mat dst = new Mat();
    Imgproc.remap(
        srcBGRA, dst,
        mapX, mapY,
        Imgproc.INTER_LINEAR,
        Core.BORDER_CONSTANT,
        new Scalar(0, 0, 0, 0) // transparent border
    );

    mapX.release();
    mapY.release();

    return dst;
  }

  /**
   * OpenCV Swirl/Vortex effect (BGRA in, BGRA out).
   * angleOffset(r) = strengthRadians * pow((1 - r/radius), exponent), applied for r < radius.
   * Positive strengthRadians swirls counter-clockwise near the center; effect decays to 0 at r=radius.
   *
   * @param srcBGRA         Input Mat (CV_8UC4, BGRA).
   * @param cx              Swirl center X in pixels.
   * @param cy              Swirl center Y in pixels.
   * @param radius          Swirl radius in pixels.
   * @param strengthRadians Max angular deflection (in radians) at the center (r=0).
   * @param exponent        Decay exponent; 1 = linear. >1 = tighter near center.
   * @return                Output Mat (CV_8UC4, BGRA) with swirl applied.
   */
  public static Mat applySwirlVortex(Mat srcBGRA,
                                     double cx, double cy,
                                     double radius,
                                     double strengthRadians,
                                     double exponent) {
    if (srcBGRA.type() != CvType.CV_8UC4) {
      throw new IllegalArgumentException("applySwirlVortex expects CV_8UC4 (BGRA) input");
    }
    int rows = srcBGRA.rows();
    int cols = srcBGRA.cols();

    Mat mapX = new Mat(rows, cols, CvType.CV_32FC1);
    Mat mapY = new Mat(rows, cols, CvType.CV_32FC1);
    float[] mapXData = new float[rows * cols];
    float[] mapYData = new float[rows * cols];

    double rMax = Math.max(1.0, radius);
    int idx = 0;
    for (int y = 0; y < rows; y++) {
      double dy = y - cy;
      for (int x = 0; x < cols; x++) {
        double dx = x - cx;
        double r = Math.hypot(dx, dy);

        float sx, sy;
        if (r < rMax) {
          double norm = 1.0 - (r / rMax);
          double weight = Math.pow(Math.max(0.0, norm), Math.max(0.0, exponent));
          double angle = Math.atan2(dy, dx) + strengthRadians * weight;

          double cs = Math.cos(angle);
          double sn = Math.sin(angle);
          sx = (float) (cx + r * cs);
          sy = (float) (cy + r * sn);
        } else {
          sx = (float) x;
          sy = (float) y;
        }

        mapXData[idx] = sx;
        mapYData[idx] = sy;
        idx++;
      }
    }

    mapX.put(0, 0, mapXData);
    mapY.put(0, 0, mapYData);

    Mat dst = new Mat();
    Imgproc.remap(
        srcBGRA, dst,
        mapX, mapY,
        Imgproc.INTER_LINEAR,
        Core.BORDER_CONSTANT,
        new org.opencv.core.Scalar(0, 0, 0, 0) // transparent borders
    );

    mapX.release();
    mapY.release();
    return dst;
  }

  /**
   * Convenience combiner: Psychedelic HSV then Kaleidoscope.
   * - hueCyclesPerLoop: number of hue spins per [0,1) phase loop
   * - kaleidoTurnsPerLoop: kaleidoscope rotation turns per loop
   */
  public static Mat applyPsychedelicThenKaleido(Mat srcBGRA,
                                                double phase,
                                                double hueCyclesPerLoop,
                                                int segments,
                                                double kaleidoTurnsPerLoop,
                                                double satBase, double satAmp,
                                                double valBase, double valAmp) {
    Mat psy = applyPsychedelicHSV(srcBGRA, hueCyclesPerLoop, phase, satBase, satAmp, valBase, valAmp);
    double angleOffset = (phase * kaleidoTurnsPerLoop) * 2.0 * Math.PI;
    Mat out = applyKaleidoscope(psy, segments, angleOffset);
    psy.release();
    return out;
  }
}
