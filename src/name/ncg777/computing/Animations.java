package name.ncg777.computing;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

public class Animations {
  public static void Animation20241225_1(String filepath) throws IOException {
    int width = 500;
    int height = 1000;
    
    GraphicsFunctions.writeAnimation(filepath, () -> new Enumeration<BufferedImage>() {
    
    int fps = 30;
    double dur = 60;
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
                (t) -> 0.5+j*0.4*Math.sin(j*Math.PI*0.25+ i*normalized_time*90*Math.PI)*(0.5-0.5*Math.cos(Math.PI*0.125+t*4.0*Math.PI)), 
                (t) -> 0.5-j*0.4*Math.cos(-i*normalized_time*90*Math.PI)*(0.5+0.5*Math.sin(Math.PI*0.125+-t*4.0*Math.PI)),
                0.0,
                1.0,
                (t) -> 0.0,
                (t) -> 0.5,
                (t) -> Double.valueOf(width),
                (t) -> Double.valueOf(width),
                (t) -> 0.075+(0.075*
                    Math.sin((45*Math.PI*(t))*Math.sin(22.5*Math.PI*normalized_time))),
                (t) -> new Color(
                    (int)(255.0*(0.5-j*0.5*(Math.cos(2.0*Math.PI*(6*(t-i*normalized_time)))))), 
                    (int)(255.0*(0.5+j*0.5*(Math.sin(2.0*Math.PI*(6*(t+i*normalized_time)))))), 
                    128,
                    4
                   ),
                (t) -> 0.0025+0.00225*Math.sin(Math.PI*8.0*t)
                );
          }
        }
        
        k++;
        System.out.println(Integer.toString(k) + " of " +  Integer.toString(upper));
        return img;
      }
    });
  }
}
