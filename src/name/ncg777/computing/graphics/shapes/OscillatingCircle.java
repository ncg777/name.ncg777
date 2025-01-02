package name.ncg777.computing.graphics.shapes;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Path2D;
import java.util.Arrays;

public class OscillatingCircle implements Shape {
  private final double centerX;
  private final double centerY;
  private final double baseRadius;
  private final double theta;
  private final double[] amplitudes; // amplitudes for each partial (index n corresponds to partial
                                     // frequency n+1)
  private final Path2D.Double path; // The path representing the shape
  private final Rectangle2D bounds; // The bounding rectangle of the shape

  /**
   * Constructs a PartialShape with specified center, base radius, and amplitudes for partials.
   *
   * @param centerX The x-coordinate of the center of the shape.
   * @param centerY The y-coordinate of the center of the shape.
   * @param baseRadius The base radius (r0) of the shape.
   * @param amplitudes An array of amplitudes for each partial.
   */
  public OscillatingCircle(double centerX, double centerY, double baseRadius, double[] amplitudes, double theta) {
    this.centerX = centerX;
    this.centerY = centerY;
    this.baseRadius = baseRadius;
    this.theta = theta;
    this.amplitudes = Arrays.copyOf(amplitudes, amplitudes.length);
    this.path = createShapePath();
    this.bounds = path.getBounds2D();
  }

  /**
   * Creates the Path2D shape by computing points around the circle with radius varying according to
   * the sum of partials.
   *
   * @return A Path2D.Double object representing the shape.
   */
  private Path2D.Double createShapePath() {
    Path2D.Double shapePath = new Path2D.Double();
    int numSamples = 720; // Number of samples around the circle for high precision
    double deltaTheta = 2 * Math.PI / numSamples;
    // Start angle
    double theta = 0.0;
    // Compute the first point
    double radius = computeRadius(theta+this.theta);
    double x = centerX + radius * Math.cos(theta);
    double y = centerY + radius * Math.sin(theta);
    shapePath.moveTo(x, y);
    // Compute points around the circle
    for (int i = 1; i <= numSamples; i++) {
      theta = i * deltaTheta;
      radius = computeRadius(theta+this.theta);
      x = centerX + radius * Math.cos(theta);
      y = centerY + radius * Math.sin(theta);
      shapePath.lineTo(x, y);
    }
    shapePath.closePath();
    return shapePath;
  }

  /**
   * Computes the radius at a given angle theta by summing the contributions of each partial.
   *
   * @param theta The angle in radians at which to compute the radius.
   * @return The radius at angle theta.
   */
  private double computeRadius(double theta) {
    double radius = baseRadius;
    for (int n = 0; n < amplitudes.length; n++) {
      int frequency = (n*2); // Partial frequencies are 1f, 2f, 3f, ..., nf
      double amplitude = amplitudes[n];
      radius += baseRadius * amplitude * (0.5-0.5*Math.cos(frequency * theta));
    }
    return radius;
  }

  @Override
  public Rectangle2D getBounds2D() {
    return bounds;
  }

  @Override
  public Rectangle getBounds() {
    return bounds.getBounds();
  }

  @Override
  public boolean contains(double x, double y) {
    return path.contains(x, y);
  }

  @Override
  public boolean contains(Point2D p) {
    return path.contains(p);
  }

  @Override
  public boolean intersects(double x, double y, double w, double h) {
    return path.intersects(x, y, w, h);
  }

  @Override
  public boolean intersects(Rectangle2D r) {
    return path.intersects(r);
  }

  @Override
  public boolean contains(double x, double y, double w, double h) {
    return path.contains(x, y, w, h);
  }

  @Override
  public boolean contains(Rectangle2D r) {
    return path.contains(r);
  }

  @Override
  public PathIterator getPathIterator(AffineTransform at) {
    return path.getPathIterator(at);
  }

  @Override
  public PathIterator getPathIterator(AffineTransform at, double flatness) {
    return path.getPathIterator(at, flatness);
  }
}
