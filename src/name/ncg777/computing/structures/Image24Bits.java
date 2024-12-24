package name.ncg777.computing.structures;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import name.ncg777.maths.Matrix;
import name.ncg777.maths.MatrixOfIntegers;

public class Image24Bits extends Matrix<Pixel24Bits> {

  public MatrixOfIntegers toMatrixOfIntegers() {
    var o = new MatrixOfIntegers(this.m, this.n, 0);
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
          o.set(j, i,this.get(i,j).toInteger());
      }
    }
    return o;
    
  }
  
  public BufferedImage toBufferedImage() {
    BufferedImage image = new BufferedImage(n, m, BufferedImage.TYPE_INT_RGB);

    for (int y = 0; y < m; y++) {
        for (int x = 0; x < n; x++) {
            image.setRGB(x, y,this.get(y,x).toInteger());
        }
    }

    return image;
  }
  
  public void writeToPNG(String path) throws IOException {
      File output = new File(path);
      ImageIO.write(this.toBufferedImage(), "png", output);
  }
}
