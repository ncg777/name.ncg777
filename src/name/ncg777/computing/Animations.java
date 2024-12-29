package name.ncg777.computing;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.function.Function;

import name.ncg777.maths.HadamardMatrix;

public class Animations {
  public static Enumeration<BufferedImage> Animation20241225_1(int width, int height, int fps, double dur) {
    return new Enumeration<BufferedImage>() {
      int upper = (int)(dur*fps);
      int k = 0;
      
      public boolean hasMoreElements() {
          return k<upper;
      }
  
      public BufferedImage nextElement() {
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
                  (t) -> new Color(
                      (int)Math.round(128.0-127.0*(j*Math.cos(Math.PI*(3.0*t-i*normalized_time)))), 
                      (int)Math.round(128.0+127.0*(j*Math.sin(Math.PI*(3.0*t+i*normalized_time)))), 
                      128,
                      16
                     ),
                  (t) -> 0.00075+0.0007*Math.sin(Math.PI*2.0*t + Math.PI*Math.sin(80.0*Math.PI*normalized_time))
                  );
              g.rotate(40.0*(double)k*2*Math.PI/(double)upper, width/2.0, height/2.0);
            }
          }
          
          System.out.print("\r" + Integer.toString(++k) + " of " +  Integer.toString(upper));
          return img;
        }
    };
  }
  
  public static Enumeration<BufferedImage> Animation20241225_2(int width, int height, int fps, double dur) {
    return new Enumeration<BufferedImage>() {
      int upper = (int)(dur*fps);
      int k = 0;
  
      public boolean hasMoreElements() {
          return k<upper;
      }
  
      public BufferedImage nextElement() {
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
          
          System.out.print("\r" + Integer.toString(++k) + " of " +  Integer.toString(upper));
          return img;
        }
      };
   }
  public static Enumeration<BufferedImage> Animation20241225_3(int width, int height, int fps, double dur) {
    return new Enumeration<BufferedImage>() {
      int upper = (int)(dur*fps);
      int k = 0;
  
      public boolean hasMoreElements() {
          return k<upper;
      }
  
      public BufferedImage nextElement() {
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
                  (int)(128.0-Math.sin(Math.PI/4+p*4.0*Math.PI)*127.0),
                  (int)(128.0+Math.cos(p*8.0*Math.PI)*127.0),
                  (int)(127.0*(1.0+Math.tanh(25.0*(0.5-r1-0.025*(Math.sin(th*12.0 -t*Math.PI*6.0)))))));
              }, 
              width, height);
          
          System.out.print("\r" + Integer.toString(++k) + " of " +  Integer.toString(upper));
          return img;
        }
      };
   }
  
  public static Enumeration<BufferedImage> Hadamard20241228_1(int n, int width, int height, int fps, double dur) {
    var m = HadamardMatrix.getMatrix(n);
    
    return new Enumeration<BufferedImage>() {
      int upper = (int)(dur*fps);
      int k = 0;
  
      public boolean hasMoreElements() {
          return k<upper;
      }
  
      public BufferedImage nextElement() {
          final double t = (double) k/(double)upper;
          final Function<Double,Double> _f = (Double r) -> 3.0*(1.0-r)*r;
          var img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
          var g = img.createGraphics();
          int dim = m.columnCount();
          g.rotate(Math.PI/4.0-2.0*_f.apply(t)*Math.PI*2.0,width/2,height/2);
          GraphicsFunctions.drawColorField2D(g, 
              (_x,_y) -> {
                double th = Math.atan2(_x, _y);
                Double r = Math.sqrt((Math.pow(_x, 2.0) + Math.pow(_y, 2.0))/2.0);
                
                Double x = r*Math.cos(th+2.0*Math.PI*_f.apply(r));
                Double y = r*Math.sin(th+2.0*Math.PI*_f.apply(r));
                
                //System.out.println(Double.toString(x) + ", " + Double.toString(y));
                double v = m.get(
                    (int)((0.5+x*0.5)*(double)(dim-1)), 
                    (int)((0.5+y*0.5)*(double)(dim-1))).doubleValue();
                var rfadestart = 0.5;
                var rfadeend = 0.7;
                return new Color(
                  (int)((0.75-0.25*Math.cos(2.0*Math.PI*t))*((1.0-r)*(v*0.5+0.5)*255.0)),
                  (int)((0.75+0.25*Math.cos(2.0*Math.PI*t))*((1.0-r)*(v*0.5+0.5)*255.0)),
                  0,
                  r > rfadeend ? 0 : (
                      r < rfadestart ? 255 : 
                          ((int)(255.0*
                              (1.0-(
                                  (r-rfadestart)/(rfadeend-rfadestart)
                              ))))
                    ));
              }, 
              width, height);
          int digits = (int)(Math.floor(
              Math.log10(
                  Integer.valueOf(upper).doubleValue()
              )
          )+1.0);
          String fmtStr ="%0" + Integer.toString(digits)+ "d";
          System.out.print("\r" + 
              String.format(fmtStr, ++k) + " of " +  Integer.toString(upper)
          );
          
          return img;
        }
      };
   }
}
