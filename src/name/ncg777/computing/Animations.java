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
        final int i = _i;
        GraphicsFunctions.drawParametric2D(
                g, 
                (t) -> 0.5+0.45*(0.5+0.25*Math.cos(normalized_time*2*Math.PI))*Math.sin(t*2*Math.PI), 
                (t) -> 0.5+0.45*(0.5+0.25*Math.sin(normalized_time*2*Math.PI))*Math.cos(t*2*Math.PI),
                0.0,
                1.0,
                (t) -> Math.sin((double)i*Math.PI*(2.0/3.0) + normalized_time*4*Math.PI)*0.1,
                (t) -> Math.cos((double)i*Math.PI*(2.0/3.0) + normalized_time*4*Math.PI)*0.1,
                (t) -> Double.valueOf(width),
                (t) -> Double.valueOf(height),
                (t) -> 0.25+(0.2*
                    Math.sin((4*2*Math.PI*4*t)+Math.cos(2*Math.PI*normalized_time))),
                (t) -> new Color(
                    (int)(255.0*(0.5+(0.5*Math.sin((t-normalized_time)*2.0*Math.PI)))), 
                    (int)(255.0*(0.5+(0.5*Math.cos((t+normalized_time)*2.0*Math.PI)))), 
                    128,
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
