package name.ncg777.computing.structures;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import javax.imageio.ImageIO;

import name.ncg777.maths.MatrixOfIntegers;

public class Image32Bits extends MatrixOfIntegers {
  public Image32Bits(int width, int height) {
    this(width, height, new Pixel32Bits(0));
  }
  
  public Image32Bits(int width, int height, Pixel32Bits fill) {
    super(height, width, fill.toInteger());
  }
  
  public Image32Bits(int width, int height, Color fill) {
    super(height, width, new Pixel32Bits(fill).toInteger());
  }
  
  public BufferedImage toBufferedImage() {
    BufferedImage image = new BufferedImage(n, m, BufferedImage.TYPE_INT_ARGB);

    for (int y = 0; y < m; y++) {
        for (int x = 0; x < n; x++) {
            
            image.setRGB(x, y,this.get(y,x));
        }
    }

    return image;
  }
  
  public Pixel32Bits getPixel(int i, int j) {
    return new Pixel32Bits(this.get(j, i));
  }
  
  public void writeToPNG(String path) throws IOException {
      File output = new File(path);
      ImageIO.write(this.toBufferedImage(), "png", output);
  }
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Double> x, 
      Function<Double,Double> y, 
      double from_inclusive, 
      double to_exclusive,
      double scale,
      Function<Double,Double> width,
      Function<Double, Color> color, 
      double delta) { 
    if(to_exclusive < from_inclusive && delta > 0) delta *= -1.0;
    
    g.setStroke(new BasicStroke(1.0f));
    
    for(double t=from_inclusive; t<to_exclusive; t+=delta) {
      Color c = color.apply(t);
      g.setColor(c);
      g.setPaint(c);
      
      double A_x = scale*x.apply(t);
      double A_y = scale*y.apply(t);
      double B_x = scale*x.apply(t+delta);
      double B_y = scale*y.apply(t+delta);
      
      double x_ccw = A_x - (B_y-A_y);
      double y_ccw = A_y + (B_x-A_x);
      double x_cw = A_x + (B_y-A_y);
      double y_cw = A_y - (B_x-A_x);
      
      double f = (scale*width.apply(t)) /
          (2.0*Math.sqrt(Math.pow(B_x-A_x, 2.0) + Math.pow(B_y-A_y, 2.0)));
      
      g.fillRect((int)A_x,(int)A_y,1,1);
      
      g.draw(
          
          new Line2D.Double(
              A_x, 
              A_y, 
              A_x+f*(x_ccw-A_x), 
              A_y+f*(y_ccw-A_y)
          ));
      
      g.draw(
          new Line2D.Double(
              A_x, 
              A_y, 
              A_x+f*(x_cw-A_x), 
              A_y+f*(y_cw-A_y)
          ));
    }
  }
  
  private static double defaultFrom = 0.0;
  private static double defaultTo = 1.0;
  private static double defaultWidth = 1.0;
  private static double defaultScale = 1.0;
  private static Color defaultColor = Color.BLACK;
  private static double defaultDelta = 0.0000005;
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Double> x, 
      Function<Double,Double> y) {
    drawParametric2D(g, x, y, defaultFrom, defaultTo, defaultScale, (t) -> defaultWidth, (t) -> defaultColor, defaultDelta);
  }
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Double> x, 
      Function<Double,Double> y, 
      double from_inclusive, 
      double to_exclusive) {
    drawParametric2D(g, x, y, from_inclusive, to_exclusive, defaultScale, (t) -> defaultWidth, (t) -> defaultColor, defaultDelta);
  }
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Double> x, 
      Function<Double,Double> y, 
      double from_inclusive, 
      double to_exclusive,
      double scale) {
    drawParametric2D(g, x, y, from_inclusive, to_exclusive, scale, (t) -> defaultWidth, (t) -> defaultColor, defaultDelta);
  }
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Double> x, 
      Function<Double,Double> y, 
      double from_inclusive, 
      double to_exclusive,
      double scale,
      Function<Double,Double> width) {
    drawParametric2D(g, x, y, from_inclusive, to_exclusive, scale, width, (t) -> defaultColor, defaultDelta);
  }
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Double> x, 
      Function<Double,Double> y, 
      double from_inclusive, 
      double to_exclusive,
      double scale,
      Function<Double,Double> width,
      Function<Double, Color> color) {
    drawParametric2D(g, x, y, from_inclusive, to_exclusive, scale, width, color, defaultDelta);
  }
  
}
