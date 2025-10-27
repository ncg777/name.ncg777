package name.ncg777.computing.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import name.ncg777.computing.graphics.GraphicsFunctions.Cartesian;
import name.ncg777.computing.graphics.GraphicsFunctions.Polar;
import name.ncg777.computing.graphics.shapes.OscillatingCircle;
import name.ncg777.computing.structures.ImmutableDoubleArray;
import name.ncg777.maths.HadamardMatrix;
import name.ncg777.maths.MatrixOfDoubles;
import name.ncg777.maths.enumerations.MixedRadixEnumeration;
import name.ncg777.maths.relations.FiniteHomoRelation;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Animations {
  public static class Helpers {
    public static record BivariateNormalProcessParams(Graphics2D g, int individual, double x, double y, double t, double life) {};

    public static Enumeration<Mat> BivariateNormalProcess(
        Consumer<BivariateNormalProcessParams> drawf, 
        RealDistribution lifetime, 
        int nb, 
        int width, 
        int height, 
        double fps, 
        double dur) {
      List<List<Consumer<Graphics2D>>> df = new ArrayList<List<Consumer<Graphics2D>>>();
      int upper = (int)(dur*fps);

      for(int i=0;i<upper;i++) {
        df.add(new ArrayList<>());
      }

      double[] means = {0.0,0.0};
      double va = 1.0/16.0;
      double[][] cov = {{va,0.0},{0.0,va}};
      List<Double> lifetimes = new ArrayList<Double>();
      for(int i=0;i<nb;i++) {
        double life = lifetime.sample();
        lifetimes.add(life);
      }

      var mnd = new MultivariateNormalDistribution(means, cov);
      var startd = new UniformIntegerDistribution(0, (int)(-1.0+(dur*fps)));

      for(int _i=0;_i<nb;_i++) {
        final int i = _i;
        int f = startd.sample();

        double x=0.0;
        double y=0.0;
        var s = mnd.sample();
        x = width*(0.5+0.5*s[0]);
        if(x < width*0.05) x = width*0.05;
        if(x > width*0.95) x = width*0.95;
        y = height*(0.5+0.5*s[1]);
        if(y < height*0.05) y = height*0.05;
        if(y > height*0.95) y = height*0.95;
        final double _x = x;
        final double _y = y;
        int len = (int)(fps*lifetimes.get(i));  
        for(int _j=0;_j<len;_j++) {
          final int j = _j;
          int _zzz = f+j;
          while(_zzz < 0) _zzz += upper;
          while(_zzz >= upper) _zzz -= upper;
          final int zzz = _zzz;
          df.get(zzz).add((Graphics2D g) -> {
            drawf.accept(new BivariateNormalProcessParams(g, i, _x, _y, (double)(zzz)/(double)upper, (double)j/(double)len));
          });
        }
      };

      return new Enumeration<Mat>() {
        int k = 0;

        public boolean hasMoreElements() {
          return k<upper;
        }
        public Mat nextElement() {
          var img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
          var g = img.createGraphics();

          for(var f : df.get(k)) {
            f.accept(g);
          }

          ++k;
          return GraphicsFunctions.bufferedImageToMat(img);
        }
      };
    }
  }

  /**
   * Neon radial sine isoclines animation with multiple random components and phase noise.
   * - Background is black, colors are vivid (neon-like) using HSB with high saturation/value.
   * - The scalar field is a sum of radial sine waves with randomized frequencies/amplitudes.
   * - Isoclines are rendered by mapping the field through a high-contrast stripe function.
   * - Additional randomized, looping phase noise is applied (angular, radial, and temporal).
   * - The animation loops seamlessly in time (use of integer time cycles).
   *
   * Contract
   * - Inputs: canvas size (width/height), timing (fps/dur), component count, isocline density,
   *           stripe thickness, noise amount, RNG seed for determinism.
   * - Output: Enumeration<Mat> of BGRA frames sized width x height.
   * - Error modes: none (parameters are sanitized where needed).
   *
   * @param width           frame width
   * @param height          frame height
   * @param fps             frames per second
   * @param dur             duration in seconds
   * @param components      number of radial sine components to sum (e.g., 4..9)
   * @param isoBands        number of isocline bands across the normalized field (e.g., 24..96)
   * @param lineThickness   thickness control for isoclines in [0.02..0.5] (smaller = thinner lines)
   * @param noiseAmount     phase noise amplitude (radians), e.g., 0.05..0.35
   * @param seed            RNG seed for reproducibility
   * @return                frames enumeration
   */
  public static Enumeration<Mat> radialSineIsoclinesNeon(
      int width, int height,
      double fps, double dur,
      int components,
      int isoBands,
      double lineThickness,
      double noiseAmount,
      long seed) {
    final int upper = (int) Math.max(1, Math.round(dur * fps));

    // Bounds for frequencies/amplitudes
    // Lower frequencies for slower, smoother radial structures
    final int fMin = 1;           // integer cycles across unit radius (ensures clean rings)
    final int fMax = 4;
    final double aMin = 0.20;
    final double aMax = 0.85;

    // Time and angular noise integer cycle bounds (ensures seamless loops)
    // Reduce to make motion slower/softer
    final int tCyclesMin = 0, tCyclesMax = 2;     // time cycles per loop for each component
    final int nThetaMin = 1, nThetaMax = 4;       // angular noise waves
    final int nTimeMin  = 1, nTimeMax  = 2;       // temporal noise waves
    final int nRadMin   = 0, nRadMax   = 2;       // radial noise waves

    final Random rng = new Random(seed);

    // Per-component randomized parameters
  final int[] freq = new int[components];
    final double[] amp  = new double[components];
    final double[] phi0 = new double[components];
    final int[] tCycles = new int[components];
    final int[] nTheta  = new int[components];
    final int[] nTime   = new int[components];
    final int[] nRad    = new int[components];
    final double[] nPhi = new double[components];

    double ampSum = 0.0;
    for (int i = 0; i < components; i++) {
  freq[i] = fMin + rng.nextInt(Math.max(1, fMax - fMin + 1));
      amp[i]  = aMin + (aMax - aMin) * rng.nextDouble();
      // balance amplitudes to avoid over-saturation
      amp[i] *= 1.0 / Math.max(1.0, Math.sqrt(components));
      ampSum += Math.abs(amp[i]);

      phi0[i] = rng.nextDouble() * 2.0 * Math.PI;
      tCycles[i] = tCyclesMin + rng.nextInt(Math.max(1, tCyclesMax - tCyclesMin + 1));
      nTheta[i]  = nThetaMin  + rng.nextInt(Math.max(1, nThetaMax - nThetaMin + 1));
      nTime[i]   = nTimeMin   + rng.nextInt(Math.max(1, nTimeMax  - nTimeMin  + 1));
      nRad[i]    = nRadMin    + rng.nextInt(Math.max(1, nRadMax   - nRadMin   + 1));
      nPhi[i]    = rng.nextDouble() * 2.0 * Math.PI;
    }
    final double ampNorm = ampSum > 1e-9 ? ampSum : 1.0;

    final double lt = Math.max(0.01, Math.min(0.75, lineThickness));
  // Contrast control for core line and a wider "glow" skirt
  final double gammaCore = 1.5; 
  final double gammaGlow = 2.2;

    return new Enumeration<Mat>() {
      int k = 0;

      @Override
      public boolean hasMoreElements() { return k < upper; }

      @Override
      public Mat nextElement() {
        if (!hasMoreElements()) throw new java.util.NoSuchElementException();
  // Use (upper-1) in the denominator so last frame hits phase=1.0 exactly,
  // making frame 0 and the final frame identical for a perfect loop.
  final double phase = (upper > 1) ? ((double) k) / ((double) (upper - 1)) : 0.0; // [0,1]

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Fill black background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        // Draw neon stripes with per-pixel color function
        GraphicsFunctions.drawColorField2D(g, (params) -> {
          // Normalize radius to [0,1] using r = sqrt((x^2 + y^2)/2)
          double x = params.cartesian().x();
          double y = params.cartesian().y();
          double rn = Math.sqrt((x*x + y*y) / 2.0);
          double th = params.polar().theta(); // [-pi,pi]

          // Accumulate radial sine components with phase noise
          double s = 0.0;
          for (int i = 0; i < components; i++) {
            double tTerm = 2.0 * Math.PI * (tCycles[i] * phase);
            // Scale angular noise by rn to avoid center discontinuity; keep low time cycles for smoothness
            double angNoise = Math.sin(nTheta[i] * th + 2.0 * Math.PI * (nTime[i] * phase) + nPhi[i]);
            double radNoise = Math.sin(2.0 * Math.PI * (nRad[i] * rn + (nTime[i] + 1) * phase) + 0.37 * nPhi[i]);
            double noise = noiseAmount * (rn * angNoise + 0.4 * radNoise);
            s += amp[i] * Math.sin(2.0 * Math.PI * (freq[i] * rn) + phi0[i] + tTerm + noise);
          }

          // Normalize field roughly to [-1,1]
          double v = s / ampNorm;

          // Stripe function: bright where sin(pi * isoBands * v) ~ 0
          double stripeArg = isoBands * v;
          double line = Math.abs(Math.sin(Math.PI * stripeArg)); // [0,1]
          // High-contrast core plus softer skirt for neon-like glow
          double core = Math.max(0.0, 1.0 - (line / lt));
          core = Math.pow(core, gammaCore);
          double glow = Math.max(0.0, 1.0 - (line / (lt * 2.8)));
          glow = Math.pow(glow, gammaGlow);
          double intensity = Math.min(1.0, core + 0.45 * glow);

          // Soft edge fade to remove border discontinuity
          double rfStart = 0.985, rfEnd = 1.0;
          if (rn > rfStart) {
            double t = Math.min(1.0, Math.max(0.0, (rn - rfStart) / (rfEnd - rfStart)));
            intensity *= (1.0 - t);
          }

          // Neon HSB color: vivid, time/space-varying, and continuous across the angle seam.
          // IMPORTANT: Avoid using raw theta (th/(2π)) directly to prevent wrap discontinuity.
          // Use only continuous functions of angle: sin(th*k), cos(th*k) which have no 2π seam.
          double base = 0.25 * phase; // integer-cycle ramp guarantees loop
          // Angle harmonics (continuous around 2π)
          double ang1 = Math.sin(th);           // 1-fold
          double ang2 = Math.cos(2.0 * th);     // 2-fold
          double ang3 = Math.sin(3.0 * th);     // 3-fold
          // Time harmonics (integer cycles -> seamless)
          double t1 = Math.sin(2.0 * Math.PI * (1.0 * phase));
          double t2 = Math.cos(2.0 * Math.PI * (2.0 * phase));
          // Compose hue from smooth ingredients
          double hue = base
                     + 0.24 * rn
                     + 0.18 * v
                     + 0.12 * ang1
                     + 0.08 * ang2
                     + 0.06 * ang3
                     + 0.07 * t1
                     + 0.05 * t2
                     + 0.06 * Math.sin(2.0 * Math.PI * (0.25 * rn + 1.0 * phase));
          hue = hue - Math.floor(hue); // wrap [0,1)
          float sat = (float)Math.min(1.0, 0.9 + 0.1 * intensity); // pop on the lines
          // Keep background black: no base brightness, only from intensity/glow
          float bri = (float) Math.min(1.0, 0.95 * intensity + 0.35 * glow);
          Color c = Color.getHSBColor((float) hue, sat, bri);
          return new Color(c.getRed(), c.getGreen(), c.getBlue(), 255);
        }, width, height);

        k++;
        return GraphicsFunctions.bufferedImageToMat(img);
      }
    };    
  }

  /**
   * Convenience overload with sensible defaults for a quick neon radial isoclines animation.
   * components=6, isoBands=48, lineThickness=0.12, noiseAmount=0.18, seed=randomized.
   */
  public static Enumeration<Mat> radialSineIsoclinesNeon(
      int width, int height,
      double fps, double dur) {
    long seed = System.nanoTime();
  return radialSineIsoclinesNeon(width, height, fps, dur,
    24, 3, 0.333, 0.95, seed);
  }

  /**
   * 20241225
   * 
   * @param width
   * @param height
   * @param fps
   * @param dur
   * @return
   */
  public static Enumeration<Mat> spiral(int width, int height, double fps, double dur) {
    return new Enumeration<Mat>() {
      int upper = (int)(dur*fps);
      int k = 0;

      public boolean hasMoreElements() {
        return k<upper;
      }

      public Mat nextElement() {
        final double t = ((double) k)/((double)upper);
        var img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        var g = img.createGraphics();

        GraphicsFunctions.drawColorField2D(g, 
            (params) -> {
              double x = params.cartesian().x();
              double y = params.cartesian().y();
              var b =3.0;
              var th = Math.atan2(y, x);
              var s = (0.5+(th/Math.PI)*0.5);
              var r1 = Math.sqrt((Math.pow(x,2.0)+Math.pow(y,2.0))/2.0);
              var r1p = ((Math.cos(t*4.0*Math.PI)))*r1;
              var r2 = (t - b*s);
              var p = 4.0*Math.cos(2.0*Math.PI*(1.0*r2+2.0*r1p));
              return new Color(
                  (int)(128.0-Math.sin(p*2.0*Math.PI)*127.0),
                  (int)(128.0-Math.sin(p*2.0*Math.PI)*127.0),
                  (int)(128.0-Math.sin(p*2.0*Math.PI)*127.0),
                  r1 < 0.7 ? 255 : 0
                  );
            }, 
            width, height);
        ++k;
        return GraphicsFunctions.bufferedImageToMat(img);
      }
    };
  }

  /**
   * 20241228
   * 
   * @param n
   * @param width
   * @param height
   * @param fps
   * @param dur
   * @param interpolate
   * @return
   */
  public static Enumeration<Mat> hadamard(int n, int width, int height, double fps, double dur, boolean interpolate) {
    final MatrixOfDoubles mat0 = HadamardMatrix.getMatrix(n).toMatrixOfDoubles();
    mat0.apply((v) -> v==1.0 ? 1.0 : 0);
    final double[][] mat = mat0.toDoubleArray();
    return new Enumeration<Mat>() {
      int upper = (int)(dur*fps);
      int k = 0;

      public boolean hasMoreElements() {
        return k<upper;
      }

      public Mat nextElement() {
        final double t = ((double) k)/((double)upper);
        var img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        var g = img.createGraphics();

        g.rotate(t*2.0*Math.PI, width/2.0,height/2);
        GraphicsFunctions.matrixDisk(
            g,
            mat,
            (coords, v) -> {
              Double r = Math.sqrt(((Math.pow(coords.cartesian().x(), 2.0) + Math.pow(coords.cartesian().y(), 2.0))/2.0));

              var rfadestart = 0.675;
              var rfadeend = 0.7;
              return new Color(
                  (int)((((0.75-0.25*Math.cos(2.0*Math.PI*t))*v)*255.0)),
                  (int)((((0.75+0.25*Math.sin(4.0*Math.PI*t))*v)*255.0)),
                  (int)((((0.75+0.25*Math.sin(8.0*Math.PI*t))*v)*255.0)),
                  r > rfadeend ? 0 : (
                      r < rfadestart ? (int)(v*255.0) : 
                        ((int)(255.0*
                            (1.0-(
                                (r-rfadestart)/(rfadeend-rfadestart)
                                ))))
                      ));

            }, 
            width, height, interpolate);
        ++k;
        return GraphicsFunctions.bufferedImageToMat(img);
      }
    };
    
    

  }

  /**
   * 20250101
   * 
   * @param nb_individuals
   * @param total_duration
   * @param mean_lifetime
   * @param lifetime_stdev
   * @param base_radius
   * @param draw_contour
   * @param nb_partials
   * @param max_partial
   * @param max_nb_turns_in_lifetime
   * @param width
   * @param height
   * @param fps
   * @return
   */
  public static Enumeration<Mat> droplets(
      int nb_individuals, 
      double total_duration, 
      double mean_lifetime,
      double lifetime_stdev,
      double base_radius,
      boolean draw_contour,
      int nb_partials,
      double max_partial,
      int max_nb_turns_in_lifetime,
      int width, 
      int height, 
      double fps) {
    int nbcols = nb_individuals; //(int)Math.ceil(Math.log((double)nb_individuals)/Math.log(2.0));
    var colord = new UniformIntegerDistribution(0, nbcols-1);
    var orientation = new UniformIntegerDistribution(0, 1);
    var nd = new NormalDistribution(0.0, 0.25);
    var pd = new NormalDistribution(0.0, max_partial);
    var td = new NormalDistribution(Math.PI,Math.PI);
    var turns = new UniformIntegerDistribution(0,max_nb_turns_in_lifetime);
    List<Integer> ind_colors = new ArrayList<Integer>();
    List<Double> radii = new ArrayList<Double>();
    List<double[]> partials = new ArrayList<double[]>();
    List<Double> thetas = new ArrayList<Double>();
    List<Double> orientations = new ArrayList<Double>();
    for(int i=0;i<nb_individuals;i++) {
      ind_colors.add(colord.sample());
      radii.add(base_radius*(0.5+0.5*nd.sample()));
      int p2 = (int)(Math.pow(2.0, nb_partials-1))+1;
      var pr = new double[p2];
      for(int j=0;j<nb_partials;j++) {
        double s = pd.sample();
        pr[(int)Math.pow(2.0, j)] = (s < 0 || s > 1) ? 0.0 : s;
      }
      partials.add(pr);

      thetas.add(td.sample());
      orientations.add((double)(turns.sample()*(-1+orientation.sample()*2)));
    }

    ColorSequence cs = new ColorSequence(nbcols);

    return Helpers.BivariateNormalProcess((params) -> {
      final double a = 0.49999*(1.0+Math.sin(-(Math.PI/2.0)+(params.life)*Math.PI*2.0));
      Color c = cs.get((int)(((double)params.individual/(double)nb_individuals)*((double)nbcols)));
      Color c2 = new Color((int)(c.getRed()*a),(int)(c.getGreen()*a),(int)(c.getBlue()*a),(int)(a*32.0));
      params.g.setPaint(c2);

      var rr = radii.get(params.individual)*a;

      var e = new OscillatingCircle(params.x-rr/2, params.y-rr/2, rr, partials.get(params.individual), orientations.get(params.individual)*(params.life*Math.PI*2.0)+ thetas.get(params.individual));
      params.g.fill(e);
      
      if(draw_contour) {
        params.g.setStroke(new BasicStroke(1.1f));
        params.g.setColor(new Color((int)(a*255.0),(int)(a*255.0),(int)(a*255.0),(int)(a*255.0)));
        params.g.draw(e);  
      }
    }, new NormalDistribution(mean_lifetime, lifetime_stdev),nb_individuals,width,height,fps,total_duration);
  }

  /**
   * Wrapper: apply psychedelic HSV color cycling and a rotating kaleidoscope in OpenCV to any base animation.
   * The wrapper loops perfectly (phase in [0,1) across dur*fps frames).
   *
   * @param baseSupplier     Supplies the base animation frames (Mat CV_8UC4).
   * @param width            Frame width.
   * @param height           Frame height.
   * @param fps              Frames per second.
   * @param dur              Duration in seconds.
   * @param kaleidoSegments  Number of kaleidoscopic mirrored segments (>= 2).
   * @param kaleidoTurnsPerLoop  Kaleidoscope rotation turns per full loop.
   * @param hueCyclesPerLoop     Hue rotations per full loop (HSV).
   * @param satBase          Base multiplicative saturation factor (1.0 = unchanged).
   * @param satAmp           Saturation modulation amplitude (e.g., 0.35).
   * @param valBase          Base multiplicative value factor (1.0 = unchanged).
   * @param valAmp           Value modulation amplitude (e.g., 0.25).
   * @return                 Enumeration of processed frames.
   */
  public static Enumeration<Mat> wrapWithKaleidoAndPsychedelic(
      Supplier<Enumeration<Mat>> baseSupplier,
      int width, int height,
      double fps, double dur,
      int kaleidoSegments,
      double kaleidoTurnsPerLoop,
      double hueCyclesPerLoop,
      double satBase, double satAmp,
      double valBase, double valAmp
  ) {
    final int upper = (int) Math.round(dur * fps);
    final Enumeration<Mat> base = baseSupplier.get();

    return new Enumeration<Mat>() {
      int k = 0;

      @Override
      public boolean hasMoreElements() {
        return k < upper && base.hasMoreElements();
      }

      @Override
      public Mat nextElement() {
        if (!hasMoreElements()) throw new java.util.NoSuchElementException();
        // Use [0,1] inclusive so last frame equals first frame for a perfect loop
        double phase = (upper > 1) ? ((double) k) / ((double) (upper - 1)) : 0.0;

        Mat src = base.nextElement(); // Expect CV_8UC4
        Mat out = GraphicsFunctions.applyPsychedelicThenKaleido(
          src,
          phase,
          hueCyclesPerLoop,
          kaleidoSegments,
          kaleidoTurnsPerLoop,
          satBase, satAmp,
          valBase, valAmp
        );

        // Free source if not reused
        src.release();
        k++;
        return out;
      }
    };
  }

  /**
   * Convenience example: take the 'hadamard' animation and post-process it with kaleidoscopic psychedelic effects.
   */
  public static Enumeration<Mat> hadamardKaleidoPsychedelic(
      int n,
      int width, int height,
      double fps, double dur,
      int kaleidoSegments,
      double kaleidoTurnsPerLoop,
      double hueCyclesPerLoop, boolean interpolate
  ) {
    return wrapWithKaleidoAndPsychedelic(
      () -> hadamard(n, width, height, fps, dur, interpolate),
      width, height, fps, dur,
      kaleidoSegments,
      kaleidoTurnsPerLoop,
      hueCyclesPerLoop,
      1.0, 0.35, // saturation: base 1.0, +/-0.35
      1.0, 0.25  // value: base 1.0, +/-0.25
    );
  }

  /**
   * Kaleidoscopic variant of the neon radial isoclines animation (colors unchanged, kaleidoscope only).
   * This preserves the neon color palette while creating a mandala-like symmetry.
   * The loop is perfect thanks to phase = k/(upper-1) in the wrapper.
   */
  public static Enumeration<Mat> radialSineIsoclinesNeonKaleido(
      int width, int height,
      double fps, double dur,
      int kaleidoSegments,
      double kaleidoTurnsPerLoop
  ) {
    // Keep colors unchanged: hueCycles=0, satAmp=0, valAmp=0
    return wrapWithKaleidoAndPsychedelic(
        () -> radialSineIsoclinesNeon(width, height, fps, dur),
        width, height, fps, dur,
        kaleidoSegments,
        kaleidoTurnsPerLoop,
        2.0,    // hueCyclesPerLoop
        0.8, 0.2, // saturation base/amp
        0.8, 0.2  // value base/amp
    );
  }

  /**
   * Full-control kaleidoscopic variant of the neon radial isoclines animation.
   */
  public static Enumeration<Mat> radialSineIsoclinesNeonKaleido(
      int width, int height,
      double fps, double dur,
      int components,
      int isoBands,
      double lineThickness,
      double noiseAmount,
      long seed,
      int kaleidoSegments,
      double kaleidoTurnsPerLoop
  ) {
    return wrapWithKaleidoAndPsychedelic(
        () -> radialSineIsoclinesNeon(width, height, fps, dur, components, isoBands, lineThickness, noiseAmount, seed),
        width, height, fps, dur,
        kaleidoSegments,
        kaleidoTurnsPerLoop,
        0.0,    // hueCyclesPerLoop (no hue shift)
        1.0, 0.0, // saturation base/amp (unchanged)
        1.0, 0.0  // value base/amp (unchanged)
    );
  }
  
  /**
   * Wrapper: apply a time-varying swirl/vortex (OpenCV remap) on top of any base animation.
   * The swirl strength follows a sine wave so the effect loops seamlessly (strength=0 at start/end).
   *
   * @param baseSupplier        Supplies base frames (CV_8UC4).
   * @param width               Frame width.
   * @param height              Frame height.
   * @param fps                 Frames per second.
   * @param dur                 Duration in seconds.
   * @param radiusFactor        Swirl radius as a fraction of min(width,height). Example: 0.48
   * @param maxStrengthTurns    Max angular deflection at center, in turns (1 turn = 2π radians).
   * @param swirlCyclesPerLoop  Number of sine cycles of strength per full loop (integer recommended).
   * @param exponent            Radial falloff exponent (>1 tighter near center).
   * @return                    Enumeration of processed frames.
   */
  public static Enumeration<Mat> wrapWithSwirlVortex(
      Supplier<Enumeration<Mat>> baseSupplier,
      int width, int height,
      double fps, double dur,
      double radiusFactor,
      double maxStrengthTurns,
      double swirlCyclesPerLoop,
      double exponent
  ) {
    final int upper = (int) Math.round(dur * fps);
    final Enumeration<Mat> base = baseSupplier.get();
    final double cx = width / 2.0;
    final double cy = height / 2.0;
    final double radius = Math.min(width, height) * radiusFactor;
    final double turnsToRadians = 2.0 * Math.PI;

    return new Enumeration<Mat>() {
      int k = 0;

      @Override
      public boolean hasMoreElements() {
        return k < upper && base.hasMoreElements();
      }

      @Override
      public Mat nextElement() {
        if (!hasMoreElements()) throw new java.util.NoSuchElementException();
        double phase = (double) k / (double) upper; // [0,1)

        // Time-varying strength that returns to 0 at the loop boundary
        double wave = Math.sin(2.0 * Math.PI * swirlCyclesPerLoop * phase);
        double strengthRadians = maxStrengthTurns * turnsToRadians * wave;

        Mat src = base.nextElement(); // Expect CV_8UC4
        Mat out = GraphicsFunctions.applySwirlVortex(
            src, cx, cy, radius, strengthRadians, exponent
        );

        src.release();
        k++;
        return out;
      }
    };
  }
  
  /**
   * 3 vertex clouds + 1 center cloud of oscillating droplets.
   * - The triangle of vertex clouds spins around the canvas center.
   * - Each cloud spins on itself (rotates its per-droplet offsets).
   * - Droplets are spread with Gaussian offsets and get time-varying, looping noise.
   * - All motion is periodic in params.t so the animation loops seamlessly.
   */
  public static Enumeration<Mat> dropletsTriangle(
      int nb_individuals,
      double total_duration,
      double mean_lifetime,
      double lifetime_stdev,
      double base_radius,
      boolean draw_contour,
      int nb_partials,
      double max_partial,
      int max_nb_turns_in_lifetime,
      int width,
      int height,
      double fps) {

    // Geometry/dispersion
    final int clouds = 4; // 3 vertices + 1 center
    final double triangleSpacingFactor = 0.31; // distance of vertex clouds from center
    final double cloudSpreadPx = Math.min(width, height) * 0.09; // static per-droplet spread
    final double noiseAmpPx = Math.min(width, height) * 0.1;    // time-varying noise amplitude
    final double triangleTurnsPerLoop = 3.0; // triangle spins once per loop

    int nbcols = nb_individuals;

    // Distributions for per-droplet params
    var colord = new UniformIntegerDistribution(0, nbcols - 1);
    var orientation = new UniformIntegerDistribution(0, 1);
    var nd = new NormalDistribution(0.0, 0.25);
    var pd = new NormalDistribution(0.0, max_partial);
    var td = new NormalDistribution(Math.PI, Math.PI);
    var turns = new UniformIntegerDistribution(0, Math.max(1, max_nb_turns_in_lifetime));
    var noiseFreqDist = new UniformIntegerDistribution(1, 5); // integer cycles per loop to ensure perfect loop

    // Per-cloud self-spin (turns per loop and direction)
    var cloudTurnsDist = new UniformIntegerDistribution(1, Math.max(1, Math.min(6, max_nb_turns_in_lifetime > 0 ? max_nb_turns_in_lifetime : 3)));
    var dirDist = new UniformIntegerDistribution(0, 1);
    double[] cloudSelfTurnsPerLoop = new double[clouds];
    double[] cloudSelfDir = new double[clouds];
    for (int c = 0; c < clouds; c++) {
      cloudSelfTurnsPerLoop[c] = cloudTurnsDist.sample(); // 1..min(6, max)
      cloudSelfDir[c] = dirDist.sample() == 0 ? -1.0 : 1.0;
    }

    // Per-droplet parameters
    List<Integer> ind_colors = new ArrayList<>();
    List<Double> radii = new ArrayList<>();
    List<double[]> partials = new ArrayList<>();
    List<Double> thetas = new ArrayList<>();       // internal shape phase
    List<Double> orientationsArr = new ArrayList<>(); // internal shape rotation speed/sign
    int[] cloudOf = new int[nb_individuals];

    // Static Gaussian offsets to create each cloud
    var offsetDist = new NormalDistribution(0.0, cloudSpreadPx);
    double[] offsetX = new double[nb_individuals];
    double[] offsetY = new double[nb_individuals];

    // Looping positional noise params per droplet
    int[] noiseFreqX = new int[nb_individuals];
    int[] noiseFreqY = new int[nb_individuals];
    double[] noisePhaseX = new double[nb_individuals];
    double[] noisePhaseY = new double[nb_individuals];

    for (int i = 0; i < nb_individuals; i++) {
      ind_colors.add(colord.sample());
      radii.add(base_radius * (0.5 + 0.5 * nd.sample()));

      int p2 = (int) (Math.pow(2.0, nb_partials - 1)) + 1;
      var pr = new double[p2];
      for (int j = 0; j < nb_partials; j++) {
        double s = pd.sample();
        pr[(int) Math.pow(2.0, j)] = (s < 0 || s > 1) ? 0.0 : s;
      }
      partials.add(pr);

      thetas.add(td.sample());
      orientationsArr.add((double) (turns.sample() * (-1 + orientation.sample() * 2)));

      // Distribute evenly across 4 clouds (0..2 vertices, 3 = center)
      cloudOf[i] = i % clouds;

      // Static Gaussian offsets (cloud spread)
      offsetX[i] = offsetDist.sample();
      offsetY[i] = offsetDist.sample();

      // Looping noise (use integer frequencies for perfect looping)
      noiseFreqX[i] = noiseFreqDist.sample();
      noiseFreqY[i] = noiseFreqDist.sample();
      // Pseudo-random phases derived from 'thetas' to avoid needing an extra RNG
      noisePhaseX[i] = (thetas.get(i) * 0.73) % (2.0 * Math.PI);
      noisePhaseY[i] = (thetas.get(i) * 1.19) % (2.0 * Math.PI);
    }

    ColorSequence cs = new ColorSequence(nbcols);
    final double cx = width / 2.0;
    final double cy = height / 2.0;
    final double triangleRadius = Math.min(width, height) * triangleSpacingFactor;

    return Helpers.BivariateNormalProcess((params) -> {
      // Global phase in [0,1); all motion is a function of this so the animation loops.
      final double phase = params.t;

      // Triangle (vertex clouds) spin
      final double triangleSpin = triangleTurnsPerLoop * 2.0 * Math.PI * phase;

      // Compute 3 triangle vertices (0..2), plus a center cloud (index 3)
      double[] triX = new double[clouds];
      double[] triY = new double[clouds];
      for (int k = 0; k < 3; k++) {
        double angle = triangleSpin + k * (2.0 * Math.PI / 3.0);
        triX[k] = cx + triangleRadius * Math.cos(angle);
        triY[k] = cy + triangleRadius * Math.sin(angle);
      }
      // Center cloud (fixed at center)
      triX[3] = cx;
      triY[3] = cy;

      // Life-based opacity/size envelope (as in droplets())
      final double a = 0.49999 * (1.0 + Math.sin(-(Math.PI / 2.0) + (params.life) * Math.PI * 2.0));
      Color c = cs.get((int) (((double) params.individual / (double) nb_individuals) * ((double) nbcols)));
      Color c2 = new Color((int) (c.getRed() * a), (int) (c.getGreen() * a), (int) (c.getBlue() * a), (int) (a * 32.0));
      params.g.setPaint(c2);

      int i = params.individual;
      double rr = radii.get(i) * a;

      // Cloud center for this droplet's assigned cloud
      int cloud = cloudOf[i];

      // Per-cloud self-rotation (turns per loop, signed)
      double selfPhi = cloudSelfDir[cloud] * cloudSelfTurnsPerLoop[cloud] * 2.0 * Math.PI * phase;
      double cosp = Math.cos(selfPhi), sinp = Math.sin(selfPhi);

      // Rotate the static Gaussian offset by cloud self-spin
      double rox = offsetX[i] * cosp - offsetY[i] * sinp;
      double roy = offsetX[i] * sinp + offsetY[i] * cosp;

      // Add smooth, looping positional noise (independent for x and y)
      double nx = noiseAmpPx * Math.sin(2.0 * Math.PI * (noiseFreqX[i] * phase) + noisePhaseX[i]);
      double ny = noiseAmpPx * Math.cos(2.0 * Math.PI * (noiseFreqY[i] * phase) + noisePhaseY[i]);

      // Final position
      double x = triX[cloud] + rox + nx;
      double y = triY[cloud] + roy + ny;

      // Keep consistent with existing droplets(): pass (center - rr/2)
      var e = new OscillatingCircle(
          x - rr / 2.0,
          y - rr / 2.0,
          rr,
          partials.get(i),
          orientationsArr.get(i) * (params.life * Math.PI * 2.0) + thetas.get(i));

      params.g.fill(e);

      if (draw_contour) {
        params.g.setStroke(new BasicStroke(1.1f));
        params.g.setColor(new Color((int) (a * 255.0), (int) (a * 255.0), (int) (a * 255.0), (int) (a * 255.0)));
        params.g.draw(e);
      }
    }, new NormalDistribution(mean_lifetime, lifetime_stdev), nb_individuals, width, height, fps, total_duration);
  }
  
  
  /**
   * 20250611
   * 
   * @param width
   * @param height
   * @param fps
   * @param dur
   * @return
   */
  public static Enumeration<Mat> f20250611(int width, int height, double fps, double dur) {
    return new Enumeration<Mat>() {
      int upper = (int)(dur*fps);
      int k = 0;
      double[] lbound = {0.0,0.0};
      double[] ubound = {1.0,1.0};
      int[] subdiv = {10,10};
      double scaleX = (double)width/2.0;
      double scaleY = (double)height/2.0;
      double transX = width*0.25;
      double transY = height*0.25;
      
      public boolean hasMoreElements() {
        return k<upper;
      }

      public Mat nextElement() {
        final double ntime = 0.5+(0.5*Math.sin(Math.PI*2.0*((double) k)/((double)upper)));
        var img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        var g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.rotate(ntime*Math.PI*2.0, (double)(width/2), (double)(height/2));
        
        MixedRadixEnumeration.consumePointSet(
            lbound, 
            ubound,
            subdiv,
            (t) -> {
              g.setColor(new Color(
                  (int)((0.5+(Math.sin(t.get(0)*ntime*2*Math.PI)*0.5))*255.0), 
                  (int)((0.5+(Math.cos(t.get(0)*ntime*2*Math.PI)*0.4))*255.0), 
                  (int)((0.5+(Math.sin(t.get(1)*ntime*2*Math.PI)*0.5))*255.0), 
                  255));

              g.fill(new Ellipse2D.Double(
                  t.get(0)*scaleX+transX, 
                  t.get(1)*scaleY+transY, 
                  10*ntime+10, 
                  10*ntime+10));
            });
        ++k;
        return GraphicsFunctions.bufferedImageToMat(img);
      }
    };
  }
  
  /**
   * Animation: 2D polar grid, each branch follows a sinusoidal path in theta as a function of r,
   * using drawParametric2D. The animation spins in one (positive) direction only.
   * Both color and circle size loop endlessly and seamlessly with time (first frame == last frame).
   * Color scheme is now perfectly cyclical using HSB.
   */
  public static Enumeration<Mat> polarGridWavyBranchesParametric(int width, int height, double fps, double dur) {
    return new Enumeration<Mat>() {
      final int upper = (int)(dur * fps);
      int k = 0;
      final double[] lbound = {0.0, 0.0}; // r, theta
      final double[] ubound = {1.0, 2.0 * Math.PI};
      final int[] subdiv = {10, 10}; // radial, angular

      public boolean hasMoreElements() { return k < upper; }

      public Mat nextElement() {
        // Use a phase that loops perfectly over all frames so frame 0 == frame N
        final double phase = (double) k / (double) upper; // [0,1)
        var img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.rotate(phase * Math.PI * 2.0, (double) (width / 2), (double) (height / 2));
        final double cx = width / 2.0;
        final double cy = height / 2.0;
        final double maxR = Math.min(width, height) * 0.45;

        // Sinusoidal theta modulation parameters
        final double amplitude = 0.35; // radians (wiggle amplitude)
        final double omega = 6.0 * Math.PI; // frequency (number of wiggles along r)
        final double speed = 2.0 * Math.PI; // time speed

        MixedRadixEnumeration.consumePointSet(
          lbound,
          ubound,
          subdiv,
          (t) -> {
            double r = t.get(0);
            double theta0 = t.get(1);
            // The sinusoidal offset is a function of r, animated by phase
            double theta = theta0 + amplitude * Math.sin(omega * r + phase * speed);
            var p = new Cartesian(
                cx + (r * maxR) * Math.cos(theta),
                cy + (r * maxR) * Math.sin(theta));

            // --- Perfectly cyclical color using HSB ---
            // Color hue is a function of (r, theta, phase), so it loops in phase
            float hue = (float)((phase + r + theta0 / (2.0 * Math.PI)) % 1.0);
            float saturation = 0.9f;
            float brightness = 1.0f;
            Color cyclicalColor = Color.getHSBColor(hue, saturation, brightness);

            g.setColor(cyclicalColor);

            // Loop circle size endlessly and smoothly
            double sizePhase = (phase + r) % 1.0; // also depends on r for variety
            double rr = 10 + 10 * (0.5 + 0.5 * Math.sin(sizePhase * 2 * Math.PI));
            g.fill(new Ellipse2D.Double(p.x() - rr / 2.0, p.y() - rr / 2.0, rr, rr));
          }
        );
        ++k;
        return GraphicsFunctions.bufferedImageToMat(img);
      }
    };
  }
  
  /**
   * Returns an Enumeration<Mat> of animated frames showing a projected grid of disks,
   * with the camera orbiting the sphere and always looking at its center.
   *
   * @param width      Width of each frame.
   * @param height     Height of each frame.
   * @param density    Density of the disk grid on the sphere.
   * @param fps        Frames per second.
   * @param duration   Animation duration in seconds.
   */
  public static Enumeration<Mat> diskGridProjection(int width, int height, int density, double fps, double duration) {
    return new Enumeration<Mat>() {
      private final int frameCount = (int)Math.round(duration * fps);
      private int frame = 0;
      private final double scale = Math.min(width, height) * 0.4;

      @Override
      public boolean hasMoreElements() {
        return frame < frameCount;
      }

      @Override
      public Mat nextElement() {
        if (!hasMoreElements()) throw new java.util.NoSuchElementException();

        double t = 2 * Math.PI * frame / frameCount;
        Mat mat = new Mat(height, width, CvType.CV_8UC4, new Scalar(0, 0, 0, 0));
        drawDiskGridProjectionOnMat(mat, t, density, scale, width, height);
        frame++;
        return mat;
      }

      /**
       * Draws the current animation frame. The camera orbits the sphere and always looks at the center.
       */
      private void drawDiskGridProjectionOnMat(Mat mat, double time, int density, double renderScale, int width, int height) {
        final double sphereRadius = 1.0;
        final double baseDiskSize = 40.0;
        final double epsilon = 0.001;

        // Camera orbits the sphere, always looking at the origin.
        double camDistance = 5.0;
        // Camera position on a tilted orbit (ellipse for some vertical modulation)
        Vector3D camera = new Vector3D(
          camDistance * Math.cos(time),
          camDistance * Math.sin(time),
          2.0 * Math.sin(0.5 * time)
        );
        Vector3D center = new Vector3D(0, 0, 0);

        // Camera "look direction" is always toward the center
        Vector3D lookDir = center.subtract(camera).normalize();

        // Projection plane: passes through the center, perpendicular to the look direction
        Vector3D planeNormal = lookDir;
        Vector3D planePoint = center;

        // Spherical grid (skip poles for better spacing)
        for (int i = 1; i < density; i++) {
          double theta = Math.PI * i / density;
          for (int j = 0; j < density; j++) {
            double phi = 2 * Math.PI * j / density;
            double x = sphereRadius * Math.sin(theta) * Math.cos(phi);
            double y = sphereRadius * Math.sin(theta) * Math.sin(phi);
            double z = sphereRadius * Math.cos(theta);
            Vector3D spherePoint = new Vector3D(x, y, z);

            double[] proj;
            try {
              proj = GraphicsFunctions.perspectiveProjection(
                spherePoint, camera, planeNormal, planePoint
              );
            } catch (IllegalArgumentException e) {
              // The ray is parallel to the plane; skip this disk.
              continue;
            }

            double u = proj[0] * renderScale + width / 2.0;
            double v = proj[1] * renderScale + height / 2.0;
            double distance = proj[2];

            double diskSize = baseDiskSize / (distance + epsilon);

            // Color and alpha
            float hue = (float) (phi / (2 * Math.PI));
            float brightness = (float) Math.max(0.2, 1.0 - distance / 10.0);
            float saturation = 0.8f;
            Color color = Color.getHSBColor(hue, saturation, brightness);
            int alpha = Math.min(255, (int) (255 * (1.0 - distance / 10.0)));

            drawSolidCircle(mat, u, v, diskSize, color, alpha);
          }
        }
      }

      private void drawSolidCircle(Mat mat, double cx, double cy, double radius, Color color, int alpha) {
        Scalar rgba = new Scalar(color.getBlue(), color.getGreen(), color.getRed(), alpha);
        Point center = new Point(cx, cy);
        int thickness = -1; // filled
        Imgproc.circle(mat, center, (int) Math.round(radius), rgba, thickness, Imgproc.LINE_AA, 0);
      }
    };
  }
  
  /**
   * Animation: 3D cubic grid, projected to 2D, where grid points rotate in 3D and are
   * connected to neighbors with lines. Colors and node sizes are perfectly periodic and
   * parametrized for endless possibilities. The animation loops seamlessly.
   *
   * @param width         Width of each frame.
   * @param height        Height of each frame.
   * @param subdivisions  Number of grid subdivisions in each dimension (min 10).
   * @param fps           Frames per second.
   * @param duration      Duration in seconds.
   * @param spinX         Number of full rotations around X axis per loop.
   * @param spinY         Number of full rotations around Y axis per loop.
   * @param spinZ         Number of full rotations around Z axis per loop.
   * @param lineAlpha     Alpha (opacity) for neighbor lines [0-255].
   * @param nodeAlpha     Alpha (opacity) for grid point nodes [0-255].
   * @param colorCycles   Number of color cycles (hue turns) per loop.
   * @param morphAmount   Amplitude of morphing the cube into a "pulsing" ball (0=simple cube, 1=full morph).
   * @return              Enumeration of Mat frames.
   */
  public static Enumeration<Mat> cubicGrid3DAnimation(
      int width, int height,
      int subdivisions,
      double fps, double duration,
      double spinX, double spinY, double spinZ,
      int lineAlpha, int nodeAlpha,
      double colorCycles,
      double morphAmount
  ) {
      final int frames = (int) Math.round(duration * fps);
      final double cx = width / 2.0, cy = height / 2.0;
      final double scale = Math.min(width, height) * 0.36;

      // Define 3D grid in [−1,1]^3
      final double[] lbound = {-1.0, -1.0, -1.0};
      final double[] ubound = {1.0, 1.0, 1.0};
      final int[] subdiv = {subdivisions, subdivisions, subdivisions};

      // Get all points and neighbor relation (static, reused)
      final TreeSet<ImmutableDoubleArray> points = MixedRadixEnumeration.getPointSet(lbound, ubound, subdiv);
      final var neighbors = MixedRadixEnumeration.getNeighborRelation(lbound, ubound, subdiv);

      return new Enumeration<Mat>() {
          int k = 0;

          public boolean hasMoreElements() { return k < frames; }

          public Mat nextElement() {
              double phase = (double) k / frames; // [0,1)
              double angleX = spinX * 2 * Math.PI * phase;
              double angleY = spinY * 2 * Math.PI * phase;
              double angleZ = spinZ * 2 * Math.PI * phase;
              double morph = morphAmount * (0.5 + 0.5 * Math.sin(phase * 2 * Math.PI));

              // Precompute rotation matrix
              double[][] rot = rotationMatrix3D(angleX, angleY, angleZ);

              // Project all points to 2D, store mapping for lines and colors
              Map<ImmutableDoubleArray, double[]> projected = new HashMap<>();
              for (ImmutableDoubleArray p : points) {
                  double[] v = {p.get(0), p.get(1), p.get(2)};
                  // Morph the cube into a "pulsing" sphere (blow-your-mind mode)
                  if (morphAmount > 0.0) {
                      double r = Math.sqrt(v[0]*v[0] + v[1]*v[1] + v[2]*v[2]);
                      double s = Math.pow(r, 3);
                      double morphR = (1-morph) + morph * s;
                      for (int i = 0; i < 3; i++) v[i] *= morphR;
                  }
                  double[] rv = rotate3D(v, rot);
                  // Perspective: move camera back a bit for depth
                  double camZ = 3.0;
                  double depth = rv[2] + camZ;
                  double px = cx + scale * rv[0] / depth;
                  double py = cy + scale * rv[1] / depth;
                  projected.put(p, new double[]{px, py, rv[2]});
              }

              // Create image and draw
              BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
              Graphics2D g = img.createGraphics();
              g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

              // 1. Draw neighbor lines (behind points)
              for (var edge : neighbors) {
                  ImmutableDoubleArray a = edge.getFirst();
                  ImmutableDoubleArray b = edge.getSecond();
                  double[] pa = projected.get(a);
                  double[] pb = projected.get(b);
                  if (pa == null || pb == null) {
                      System.err.println("Missing projection for: " + (pa == null ? a : b));
                      continue; // or throw
                  }
                  // Dynamic color based on edge midpoint and time
                  double[] mid = {(a.get(0)+b.get(0))/2, (a.get(1)+b.get(1))/2, (a.get(2)+b.get(2))/2};
                  double midR = Math.sqrt(mid[0]*mid[0] + mid[1]*mid[1] + mid[2]*mid[2]);
                  float hue = (float) ((phase + colorCycles * midR + 0.55 * Math.atan2(mid[1], mid[0]) / (2*Math.PI)) % 1.0);
                  Color lineColor = Color.getHSBColor(hue, 0.7f, 1.0f);
                  g.setColor(new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), lineAlpha));
                  g.setStroke(new BasicStroke(1.2f));
                  g.draw(new java.awt.geom.Line2D.Double(pa[0], pa[1], pb[0], pb[1]));
              }

              // 2. Draw points
              for (ImmutableDoubleArray p : points) {
                  double[] pp = projected.get(p);
                  double d = Math.sqrt(p.get(0)*p.get(0) + p.get(1)*p.get(1) + p.get(2)*p.get(2));
                  float hue = (float) ((phase + colorCycles * d + 0.4 * Math.atan2(p.get(1), p.get(0)) / (2*Math.PI)) % 1.0);
                  float sat = 0.75f + 0.2f * (float)Math.sin(phase * 2 * Math.PI + d * 8);
                  float bright = 0.8f + 0.2f * (float)Math.cos(phase * 2 * Math.PI + d * 5);
                  Color c = Color.getHSBColor(hue, sat, bright);
                  int alpha = nodeAlpha;
                  int size = (int)(7 + 21 * (0.5 + 0.5 * Math.sin(2*Math.PI*phase + d*6 + pp[2]*0.8)));
                  g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
                  g.fill(new java.awt.geom.Ellipse2D.Double(pp[0]-size/2.0, pp[1]-size/2.0, size, size));
              }

              ++k;
              return GraphicsFunctions.bufferedImageToMat(img);
          }

          // 3x3 rotation matrix: Rz * Ry * Rx
          private double[][] rotationMatrix3D(double ax, double ay, double az) {
              double sx = Math.sin(ax), cx = Math.cos(ax);
              double sy = Math.sin(ay), cy = Math.cos(ay);
              double sz = Math.sin(az), cz = Math.cos(az);
              double[][] Rx = { {1,0,0}, {0,cx,-sx}, {0,sx,cx} };
              double[][] Ry = { {cy,0,sy}, {0,1,0}, {-sy,0,cy} };
              double[][] Rz = { {cz,-sz,0}, {sz,cz,0}, {0,0,1} };
              return multiply3x3(Rz, multiply3x3(Ry, Rx));
          }
          private double[][] multiply3x3(double[][] A, double[][] B) {
              double[][] r = new double[3][3];
              for (int i=0;i<3;i++) for (int j=0;j<3;j++) for (int k=0;k<3;k++) r[i][j] += A[i][k]*B[k][j];
              return r;
          }
          private double[] rotate3D(double[] v, double[][] R) {
              return new double[] {
                  R[0][0]*v[0] + R[0][1]*v[1] + R[0][2]*v[2],
                  R[1][0]*v[0] + R[1][1]*v[1] + R[1][2]*v[2],
                  R[2][0]*v[0] + R[2][1]*v[1] + R[2][2]*v[2]
              };
          }
      };
  }
  
  public static Enumeration<Mat> f20250708(int width, int height, double fps, double dur, int n) {
    return new Enumeration<Mat>() {
      final int upper = (int)(dur * fps);
      int k = 0;
      final double[] lbound = {0.0};
      final double[] ubound = {1.0};
      final int[] subdiv = {4096};
      final double scale = ((double)Math.min(width, height))*0.45;
      final int transX = width/2;
      final int transY = height/2;
      final Function<Double,Double> transformation = (Double theta) -> {
        return (1.0 + 0.25*Math.sin((2.0+1.0*(Math.sin(2.0*Math.PI*Math.sin(theta*1024.0*Math.PI)/4.0)))*theta*0.95*Math.PI))*Math.PI;
      };
      FiniteHomoRelation<ImmutableDoubleArray> nr = MixedRadixEnumeration.getNeighborRelation(lbound, ubound, subdiv);
      
      public boolean hasMoreElements() { return k < upper; }

      public Mat nextElement() {
        final double phase = (double) k / (double) upper;
        var img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var g = img.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1.0f));
        for(int i=0; i<n ;i++) {
          double delta = Math.PI*2.0/(double)n;
          g.rotate(delta, transX, transY);
          for(var tp : nr) {
            var theta1 = transformation.apply(tp.getFirst().get(0));
            var theta2 = transformation.apply(tp.getSecond().get(0));
            var r1 = (Math.sin(tp.getFirst().get(0)*2.0*Math.PI + phase*2.0*Math.PI))*scale;
            var r2 = (Math.sin(tp.getSecond().get(0)*2.0*Math.PI + phase*2.0*Math.PI))*scale;
            var p1 = GraphicsFunctions.toCartesian(new Polar(r1, theta1));
            var p2 = GraphicsFunctions.toCartesian(new Polar(r2, theta2));

            g.setColor(new Color(Color.HSBtoRGB((float)(0.5+0.5*Math.sin((float)(tp.getFirst().get(0))*Math.PI*2.0*4.0)), (float)(0.8+0.2*Math.cos(delta+Math.PI*phase*2.0)), 0.99f)));
            g.drawLine((int)Math.round(p1.x())+transX, (int)Math.round(p1.y())+transY, (int)Math.round(p2.x())+transX, (int)Math.round(p2.y())+transY);
          }
        }
        
        
        ++k;
        return GraphicsFunctions.bufferedImageToMat(img);
      }
    };
  }
  
  public static Enumeration<Mat> fftDiskAnimation(
      File wavFile,
      int width,
      int height,
      double bpm,  // tempo in beats per minute (used to calculate 8 bars' duration)
      double fps   // animation frame rate
  ) throws Exception {
  
    // === Load Audio ===
    AudioInputStream stream = AudioSystem.getAudioInputStream(wavFile);
    AudioFormat format = stream.getFormat();
  
    int sampleRate = (int) format.getSampleRate();
    int channels = format.getChannels();
    int bytesPerSample = format.getSampleSizeInBits() / 8;
    boolean isBigEndian = format.isBigEndian();
  
    byte[] audioBytes = stream.readAllBytes();
    ByteBuffer bb = ByteBuffer.wrap(audioBytes);
    bb.order(isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
  
    int totalSamples = audioBytes.length / (bytesPerSample * channels);
    double[] samples = new double[totalSamples];
  
    for (int i = 0; i < totalSamples; i++) {
        if (bytesPerSample == 2) {
            short sample = bb.getShort(i * bytesPerSample * channels);
            samples[i] = sample / 32768.0;
        } else {
            throw new UnsupportedOperationException("Only 16-bit audio supported.");
        }
    }
  
    // === FFT Parameters ===
    int windowSize = 1024;
    int hopSize = windowSize / 2;
    int fftBins = windowSize / 2;
    FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
  
    // === Compute FFT Frames ===
    List<double[]> fftFrames = new ArrayList<>();
    for (int i = 0; i + windowSize <= samples.length; i += hopSize) {
        double[] window = Arrays.copyOfRange(samples, i, i + windowSize);
        Complex[] spectrum = fft.transform(window, TransformType.FORWARD);
  
        double[] magnitudes = new double[fftBins];
        for (int j = 0; j < fftBins; j++) {
            magnitudes[j] = spectrum[j].abs();
        }
        fftFrames.add(magnitudes);
    }
  
    // === Matrix Configuration ===
    int logBands = 64;
    int beats = 8 * 4; // 8 bars, 4 beats per bar
    double secondsPerBeat = 60.0 / bpm;
    int timeCols = (int) Math.ceil(beats * secondsPerBeat * fps); // number of columns for 8 bars
    double[][] matrixData = new double[logBands][timeCols];
  
    // === Animation Time Step ===
    double fftFramesPerSecond = (double) sampleRate / hopSize;
    double fftFramesPerVideoFrame = fftFramesPerSecond / fps;
  
    return new Enumeration<>() {
        int frameIndex = 0;
        double fftCursor = 0.0;
  
        public boolean hasMoreElements() {
            return (int) fftCursor < fftFrames.size();
        }
  
        public Mat nextElement() {
            int fftIndex = (int) fftCursor;
            double[] fftFrame = fftFrames.get(fftIndex);
  
            // Shift matrix left, add new column
            for (int band = 0; band < logBands; band++) {
                for (int col = 0; col < timeCols - 1; col++) {
                    matrixData[band][col] = matrixData[band][col + 1];
                }
  
                int low = (int) Math.pow(fftBins, (double) band / logBands);
                int high = (int) Math.pow(fftBins, (double) (band + 1) / logBands);
                low = Math.max(0, Math.min(low, fftFrame.length - 1));
                high = Math.max(low + 1, Math.min(high, fftFrame.length));
  
                double avg = 0.0;
                for (int j = low; j < high; j++) avg += fftFrame[j];
                avg /= (high - low);
                matrixData[band][timeCols - 1] = avg;
            }
  
            // === Draw Rotating Disk ===
            double t = (double) frameIndex / fftFrames.size();
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.rotate(t * 2.0 * Math.PI, width / 2.0, height / 2.0);
  
            GraphicsFunctions.matrixDisk(
                g,
                matrixData,
                (coords, v) -> new Color(0, 0, (int) Math.min(255, v * 255), (int) Math.min(255, v * 255)),
                width,
                height,
                true
            );
  
            fftCursor += fftFramesPerVideoFrame;
            frameIndex++;
  
            return GraphicsFunctions.bufferedImageToMat(img);
        }
    };
  }


  
}