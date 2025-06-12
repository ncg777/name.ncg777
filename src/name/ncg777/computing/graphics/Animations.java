package name.ncg777.computing.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;

import name.ncg777.computing.graphics.GraphicsFunctions.Cartesian;
import name.ncg777.computing.graphics.shapes.OscillatingCircle;
import name.ncg777.maths.HadamardMatrix;
import name.ncg777.maths.MatrixOfDoubles;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.opencv.core.Mat;

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
    final MatrixOfDoubles mat = HadamardMatrix.getMatrix(n).toMatrixOfDoubles();
    mat.apply((v) -> v==1.0 ? 1.0 : 0);
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
      
      public boolean hasMoreElements() {
        return k<upper;
      }

      public Mat nextElement() {
        final double ntime = 0.5+(0.5*Math.sin(Math.PI*2.0*((double) k)/((double)upper)));
        var img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        var g = img.createGraphics();
        g.rotate(ntime*Math.PI*2.0, (double)(width/2), (double)(height/2));
        GraphicsFunctions.drawParametric2D(
            g,
            () -> new Cartesian(width/2, height/2),
            () -> new Cartesian(width*0.25, height*0.25),
            lbound, 
            ubound,
            subdiv,
            (ctx) -> {
              var t = ctx.t();
              var p = new Cartesian(t[0],t[1]);
              
              g.setColor(new Color(
                  (int)((0.5+(Math.sin(p.x()*ntime*2*Math.PI)*0.5))*255.0), 
                  (int)((0.5+(Math.cos(p.x()*ntime*2*Math.PI)*0.4))*255.0), 
                  (int)((0.5+(Math.sin(p.y()*ntime*2*Math.PI)*0.5))*255.0), 
                  255));

              ctx.g().fill(new Ellipse2D.Double(
                  p.x()*ctx.scale().get().x()+ctx.translate().get().x(), 
                  p.y()*ctx.scale().get().y()+ctx.translate().get().y(), 
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
        g.rotate(phase * Math.PI * 2.0, (double) (width / 2), (double) (height / 2));
        final double cx = width / 2.0;
        final double cy = height / 2.0;
        final double maxR = Math.min(width, height) * 0.45;

        // Sinusoidal theta modulation parameters
        final double amplitude = 0.35; // radians (wiggle amplitude)
        final double omega = 6.0 * Math.PI; // frequency (number of wiggles along r)
        final double speed = 2.0 * Math.PI; // time speed

        GraphicsFunctions.drawParametric2D(
          g,
          () -> new Cartesian(1, 1), // scale (already scaled in mapping)
          () -> new Cartesian(0, 0), // translate (already translated in mapping)
          lbound,
          ubound,
          subdiv,
          (ctx) -> {
            var t = ctx.t();
            double r = t[0];
            double theta0 = t[1];
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

            ctx.g().setColor(cyclicalColor);

            // Loop circle size endlessly and smoothly
            double sizePhase = (phase + r) % 1.0; // also depends on r for variety
            double rr = 10 + 10 * (0.5 + 0.5 * Math.sin(sizePhase * 2 * Math.PI));
            ctx.g().fill(new Ellipse2D.Double(p.x() - rr / 2.0, p.y() - rr / 2.0, rr, rr));
          }
        );
        ++k;
        return GraphicsFunctions.bufferedImageToMat(img);
      }
    };
  }
}