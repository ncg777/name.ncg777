package name.ncg777.computing;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Enumeration;

public class Animations {
  public static void Animation20241225_1(String filepath) throws IOException {
    int width = 1920;
    int height = 1080;
    
    GraphicsFunctions.writeAnimation(filepath, () -> new Enumeration<BufferedImage>() {
    
    int fps = 30;
    double dur = 30;
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
        for(int _i=0;_i<3;_i++) {
          final int i = _i-1;
          GraphicsFunctions.drawParametric2D(
                g, 
                (t) -> 0.5+0.4*Math.cos(((double)i)*normalized_time*8*Math.PI)*Math.sin(t*4*Math.PI), 
                (t) -> 0.5+0.4*Math.sin(normalized_time*8*Math.PI)*Math.cos(((double)i)*t*4*Math.PI),
                0.0,
                1.0,
                (t) -> Math.sin((double)i*0.5*Math.PI + Math.PI*Math.cos(normalized_time*2*Math.PI))*0.05,
                (t) -> Math.sin((double)i*0.5*Math.PI + Math.PI*Math.sin(normalized_time*2*Math.PI))*0.05,
                (t) -> Double.valueOf(width),
                (t) -> Double.valueOf(height),
                (t) -> 0.1+(0.05*
                    Math.sin((32*2*Math.PI*t)+Math.cos(2*Math.PI*normalized_time))),
                (t) -> new Color(
                    (int)(255.0*(0.5+(0.5*Math.sin((t-i*normalized_time)*2.0*Math.PI)))), 
                    (int)(255.0*(0.5+(0.5*Math.cos((t+i*normalized_time)*2.0*Math.PI)))), 
                    0,
                    255
                   )
                );
        }
        
        k++;
        System.out.println(Integer.toString(k) + " of " +  Integer.toString(upper));
        return img;
      }
    });
  }
}
