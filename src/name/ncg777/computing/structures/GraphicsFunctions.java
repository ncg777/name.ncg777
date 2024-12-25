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

public class GraphicsFunctions {
  public static void writeToPNG(BufferedImage image, String path) throws IOException {
      File output = new File(path);
      ImageIO.write(image, "png", output);
  }
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Double> x, 
      Function<Double,Double> y, 
      double from_inclusive, 
      double to_exclusive,
      Function<Double,Double> translateX,
      Function<Double,Double> translateY,
      Function<Double,Double> scaleX,
      Function<Double,Double> scaleY,
      Function<Double,Double> width,
      Function<Double, Color> color, 
      double delta) { 
    if(to_exclusive < from_inclusive && delta > 0) delta *= -1.0;
    
    g.setStroke(new BasicStroke(1.0f));
    
    for(double t=from_inclusive; t<to_exclusive; t+=delta) {
      var c = color.apply(t);
      
      g.setColor(c);
      g.setPaint(c);
      
      double sx = scaleX.apply(t);
      double sy = scaleY.apply(t);
      double tx = translateX.apply(t);
      double ty = translateY.apply(t);
      
      double A_x = sx*(x.apply(t)+tx);
      double A_y = sy*(y.apply(t)+ty);
      double B_x = sx*(x.apply(t+delta)+tx);
      double B_y = sy*(y.apply(t+delta)+ty);
      
      double x_ccw = A_x - (B_y-A_y);
      double y_ccw = A_y + (B_x-A_x);
      double x_cw = A_x + (B_y-A_y);
      double y_cw = A_y - (B_x-A_x);
      double w = width.apply(t);
      double f = w /
          (2.0*Math.sqrt(
              Math.pow(-tx+((B_x-A_x)/sx), 2.0) + 
              Math.pow(-ty+((B_y-A_y)/sy), 2.0)));
      
      g.fillRect((int)Math.round(A_x),(int)Math.round(A_y),1,1);
      
      g.draw(
          new Line2D.Double(
              A_x, 
              A_y, 
              A_x+f*sx*(x_ccw-A_x), 
              A_y+f*sy*(y_ccw-A_y)
          ));
      
      g.draw(
          new Line2D.Double(
              A_x, 
              A_y, 
              A_x+f*sx*(x_cw-A_x), 
              A_y+f*sy*(y_cw-A_y)
          ));
    }
  }
  
  private static double defaultFrom = 0.0;
  private static double defaultTo = 1.0;
  private static double defaultWidth = 1.0;
  private static double defaultTranslateX = 0.0;
  private static double defaultTranslateY = 0.0;
  private static double defaultScaleX = 1.0;
  private static double defaultScaleY = 1.0;
  private static Color defaultColor = Color.BLACK;
  private static double defaultDelta = 0.000005;
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Double> x, 
      Function<Double,Double> y) {
    drawParametric2D(g, x, y, defaultFrom, defaultTo, 
        (t) -> defaultTranslateX,
        (t) -> defaultTranslateY, 
        (t) -> defaultScaleX, 
        (t) -> defaultScaleY, 
        (t) -> defaultWidth, 
        (t) -> defaultColor, 
        defaultDelta);
  }
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Double> x, 
      Function<Double,Double> y, 
      double from_inclusive, 
      double to_exclusive) {
    drawParametric2D(g, x, y, from_inclusive, to_exclusive, 
        (t) -> defaultTranslateX,
        (t) -> defaultTranslateY, 
        (t) -> defaultScaleX, 
        (t) -> defaultScaleY, 
        (t) -> defaultWidth, 
        (t) -> defaultColor, 
        defaultDelta);
  }
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Double> x, 
      Function<Double,Double> y, 
      double from_inclusive, 
      double to_exclusive,
      Function<Double,Double> translateX,
      Function<Double,Double> translateY) {
    drawParametric2D(g, x, y, from_inclusive, to_exclusive, 
        translateX,
        translateY, 
        (t) -> defaultScaleX, 
        (t) -> defaultScaleY, 
        (t) -> defaultWidth, 
        (t) -> defaultColor, 
        defaultDelta);
  }
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Double> x, 
      Function<Double,Double> y, 
      double from_inclusive, 
      double to_exclusive,
      Function<Double,Double> translateX,
      Function<Double,Double> translateY,
      Function<Double,Double> scaleX,
      Function<Double,Double> scaleY) {
    drawParametric2D(g, x, y, from_inclusive, to_exclusive, 
        translateX,
        translateY, 
        scaleX, 
        scaleY, 
        (t) -> defaultWidth, 
        (t) -> defaultColor, 
        defaultDelta);
  }
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Double> x, 
      Function<Double,Double> y, 
      double from_inclusive, 
      double to_exclusive,
      Function<Double,Double> translateX,
      Function<Double,Double> translateY,
      Function<Double,Double> scaleX,
      Function<Double,Double> scaleY,
      Function<Double,Double> width) {
    drawParametric2D(g, x, y, from_inclusive, to_exclusive, 
        translateX,
        translateY, 
        scaleX, 
        scaleY, 
        width, 
        (t) -> defaultColor, 
        defaultDelta);
  }
  
  public static void drawParametric2D(
      Graphics2D g, 
      Function<Double,Double> x, 
      Function<Double,Double> y, 
      double from_inclusive, 
      double to_exclusive,
      Function<Double,Double> translateX,
      Function<Double,Double> translateY,
      Function<Double,Double> scaleX,
      Function<Double,Double> scaleY,
      Function<Double,Double> width,
      Function<Double,Color> color) {
    drawParametric2D(g, x, y, from_inclusive, to_exclusive, 
        translateX,
        translateY, 
        scaleX, 
        scaleY, 
        width, 
        color, 
        defaultDelta);
  }
}
