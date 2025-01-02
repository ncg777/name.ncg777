package name.ncg777.computing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import name.ncg777.maths.HadamardMatrix;
import name.ncg777.maths.MatrixOfDoubles;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;

import org.opencv.core.Mat;

public class Animations {
  public static Enumeration<Mat> Animation20241225_1(int width, int height, double fps, double dur) {
    return new Enumeration<Mat>() {
      int upper = (int)(dur*fps);
      int k = 0;

      public boolean hasMoreElements() {
        return k<upper;
      }

      public Mat nextElement() {
        double normalized_time = ((double) k)/((double)upper);
        var img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        var g = img.createGraphics();
        g.setBackground(new Color(0,0,0,0));

        var list = new ArrayList<Double>();
        list.add(-1.0);
        list.add(1.0);

        for(int _i=0;_i<2;_i++) {
          final double i = list.get(_i);
          for(int _j=0;_j<2;_j++) {
            final double j = list.get(_j);

            GraphicsFunctions.drawParametric2D(
                g, 
                (t) -> 0.5+j*0.35*Math.cos(j*Math.PI*0.125+i*normalized_time*40*Math.PI)*(0.5-0.5*Math.cos((4.0-(Math.abs(2.0-4.0*t)*2.0+2.0*(2.0+(1.0-Math.abs(1.0-2.0*normalized_time)))))*Math.PI)), 
                (t) -> 0.5-j*0.35*Math.sin(               -i*normalized_time*40*Math.PI)*(0.5+0.5*Math.sin((2.0-(Math.abs(1.0-2.0*t)*2.0+2.0*(4.0+(2.0-Math.abs(2.0-4.0*normalized_time)))))*Math.PI)),
                0.0,
                1.0,
                (t) -> 0.0,
                (t) -> 0.0,
                (t) -> Double.valueOf(width),
                (t) -> Double.valueOf(height),
                (t) -> 0.1+0.1*Math.sin(42*Math.PI*t)*Math.sin(20*Math.PI*normalized_time),
                (t,u) -> new Color(
                    (int)Math.round(128.0-127.0*(j*Math.cos(Math.PI*(3.0*t-i*normalized_time)))), 
                    (int)Math.round(128.0+127.0*(j*Math.sin(Math.PI*(3.0*t+i*normalized_time)))), 
                    128,
                    (int)(128.0*Math.pow(1.0-u,0.5))
                    ),
                (t) -> 0.00075+0.0007*Math.sin(Math.PI*2.0*t + Math.PI*Math.sin(80.0*Math.PI*normalized_time))
                );
            g.rotate(40.0*(double)k*2*Math.PI/(double)upper, width/2.0, height/2.0);
          }
        }
        ++k;
        return GraphicsFunctions.BufferedImageToMat(img);
      }
    };
  }

  public static Enumeration<Mat> Animation20241225_2(int width, int height, double fps, double dur) {
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
            (x,y) -> {
              var b = 5.0;
              var th = Math.atan2(x, y);
              var s = 0.5+(th/Math.PI)*0.5;
              var r1 = Math.sqrt((Math.pow(x,2.0)+Math.pow(y,2.0))/2.0);
              var r2 = (t - b*s);
              var p = 2.0*Math.sin(2.0*Math.PI*(1.0*r2+1.0*r1));
              return new Color(
                  (int)(128.0+Math.sin(p*8.0*Math.PI)*127.0),
                  (int)(128.0+Math.sin(Math.PI/4+p*4.0*Math.PI)*127.0),
                  (int)(128.0+Math.cos(p*2.0*Math.PI)*127.0),
                  (int)(128.0*(1.0+Math.tanh(20*(0.5-r1)))));
            }, 
            width, height);
        ++k;
        return GraphicsFunctions.BufferedImageToMat(img);
      }
    };
  }
  public static Enumeration<Mat> Animation20241225_3(int width, int height, double fps, double dur) {
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
            (x,y) -> {
              var b =3.0;
              var th = Math.atan2(x, y);
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
        return GraphicsFunctions.BufferedImageToMat(img);
      }
    };
  }

  public static Enumeration<Mat> Hadamard20241228_1(int n, int width, int height, double fps, double dur) {
    final var m = HadamardMatrix.getMatrix(n);
    return new Enumeration<Mat>() {
      int upper = (int)(dur*fps);
      int k = 0;

      public boolean hasMoreElements() {
        return k<upper;
      }

      public Mat nextElement() {
        final double t = (double) k/(double)upper;
        final Function<Double,Double> _f = (Double r) -> Math.pow(-1.0+2.0*(1.0-r)*r,3.0);
        var img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var g = img.createGraphics();
        int dim = m.columnCount();
        g.rotate((Math.PI/4.0)-(Math.PI*2.0*t),width/2,height/2);
        GraphicsFunctions.drawColorField2D(g, 
            (_x,_y) -> {
              double th = Math.atan2(0.5+0.5*_x, 0.5+0.5*_y);
              Double r = Math.sqrt((Math.pow(_x, 2.0) + Math.pow(_y, 2.0))/2.0);

              Double x = r*Math.cos(th+Math.PI*Math.sin(2.0*Math.PI*t)*_f.apply(r));
              Double y = r*Math.sin(th+Math.PI*Math.sin(2.0*Math.PI*t)*_f.apply(r));
              int i = (int)Math.floor((0.5+x*0.5)*((double)(dim)));
              int j = (int)Math.floor((0.5+y*0.5)*((double)(dim)));
              double v = (i>=dim || j >= dim) ? 0.0 : m.get(i,j).doubleValue();
              var rfadestart = 0.675;
              var rfadeend = 0.7;
              var v2 = (v*0.5+0.5);
              return new Color(
                  (int)((((0.75-0.25*Math.cos(2.0*Math.PI*t))*v2)*255.0)),
                  (int)((((0.75+0.25*Math.cos(2.0*Math.PI*t))*v2)*255.0)),
                  (int)(0.0),
                  r > rfadeend ? 0 : (
                      r < rfadestart ? (int)(v2*255.0) : 
                        ((int)(255.0*
                            (1.0-(
                                (r-rfadestart)/(rfadeend-rfadestart)
                                ))))
                      ));
            }, 
            width, height);
        ++k;     
        return GraphicsFunctions.BufferedImageToMat(img);
      }
    };
  }
  public static record MatrixDiskColorParams(double x, double y, double v, double t) {}

  /***
   * Draws a matrix of doubles on a disk.
   * 
   * @param mat Values assumed to be in [-1,1]
   * @param width
   * @param height
   * @param fps
   * @param dur
   * @return
   */
  public static Enumeration<Mat> MatrixDisk(MatrixOfDoubles mat, Function<MatrixDiskColorParams,Color> color, int width, int height, double fps, double dur) {
    int m = mat.rowCount();
    int n = mat.columnCount();

    return new Enumeration<Mat>() {
      int upper = (int)(dur*fps);
      int k = 0;

      public boolean hasMoreElements() {
        return k<upper;
      }

      public Mat nextElement() {
        final double t = (double) k/(double)upper;
        final Function<Double,Double> _f =  (Double r) -> Math.pow(-1.0+2.0*(1.0-r)*r,3.0);
        var img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var g = img.createGraphics();
        g.rotate((Math.PI/4.0)-(Math.PI*2.0*t),width/2,height/2);
        GraphicsFunctions.drawColorField2D(g, 
            (_x,_y) -> {
              double th = Math.atan2(0.5+0.5*_x, 0.5+0.5*_y);
              Double r = Math.sqrt((Math.pow(_x, 2.0) + Math.pow(_y, 2.0))/2.0);

              Double x = r*Math.cos(th+Math.PI*Math.sin(2.0*Math.PI*t)*_f.apply(r));
              Double y = r*Math.sin(th+Math.PI*Math.sin(2.0*Math.PI*t)*_f.apply(r));
              int i = (int)Math.floor((0.5+x*0.5)*((double)(m)));
              int j = (int)Math.floor((0.5+y*0.5)*((double)(n)));
              double v = (i>=m || j >= n) ? 0.0 : mat.get(i,j);
              
              return color.apply(new MatrixDiskColorParams(x,y,v,t));
            }, 
            width, height);
        ++k;     
        return GraphicsFunctions.BufferedImageToMat(img);
      }
    };
  }
  
  public static record BivariateNormalProcessParams(Graphics2D g, int individual, double x, double y, double t, double life) {};
   
  public static Enumeration<Mat> Droplets20250101_1(
      int nb_individuals, 
      double total_duration, 
      double mean_lifetime,
      double lifetime_stdev,
      double max_radius,
      int width, 
      int height, 
      double fps) {
    var colord = new UniformIntegerDistribution(1, 7);
    var nd = new NormalDistribution(0.0, 0.25);
    
    List<Integer> ind_colors = new ArrayList<Integer>();
    List<Double> radii = new ArrayList<Double>();
    
    for(int i=0;i<nb_individuals;i++) {
      ind_colors.add(colord.sample());
      radii.add(max_radius*(0.5+0.5*nd.sample()));
    }
    
    return BivariateNormalProcess((params) -> {
      final double a = 0.49999*(1.0+Math.sin(-(Math.PI/2.0)+(params.life)*Math.PI*2.0));
      int cn = ind_colors.get(params.individual);
      Color c = new Color((int)(((cn&1)/1)*(a*255.0)),(int)(((cn&2)/2)*(a*255.0)),(int)(((cn&4)/4)*(a*255.0)),(int)(32.0*a));
      params.g.setColor(c);
      params.g.setPaint(c);
      
      var rr = radii.get(params.individual)*a;
      params.g.fill(new Ellipse2D.Double(params.x-rr/2, params.y-rr/2, rr, rr));
    }, new NormalDistribution(mean_lifetime, lifetime_stdev),nb_individuals,width,height,fps,total_duration);
  }
  
  public static Enumeration<Mat> BivariateNormalProcess(Consumer<BivariateNormalProcessParams> drawf, RealDistribution lifetime, int nb, int width, int height, double fps, double dur) {
    List<List<Consumer<Graphics2D>>> df = new ArrayList<List<Consumer<Graphics2D>>>();
    int upper = (int)(dur*fps);
    
    for(int i=0;i<upper;i++) {
      df.add(new ArrayList<>());
    }
    
    double[] means = {0.0,0.0};
    double[][] cov = {{0.025,0.0},{0.0,0.025}};
    List<Double> lifetimes = new ArrayList<Double>();
    double max_lifetime = -1.0;
    for(int i=0;i<nb;i++) {
      double life = lifetime.sample();
      lifetimes.add(life);
      if(life > max_lifetime) max_lifetime = life;
    }
    
    var mnd = new MultivariateNormalDistribution(means, cov);
    var startd = new UniformIntegerDistribution(0, (int)(-1.0+((dur-max_lifetime)*fps)));
    
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
        df.get(f+j).add((Graphics2D g) -> {
          drawf.accept(new BivariateNormalProcessParams(g, i, _x, _y, (double)(f+j)/(double)upper, (double)j/(double)len));
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
        return GraphicsFunctions.BufferedImageToMat(img);
      }
    };
  }
}
