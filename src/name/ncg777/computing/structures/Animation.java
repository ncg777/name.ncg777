package name.ncg777.computing.structures;

import java.util.Enumeration;
import java.util.function.Supplier;

import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Animation {
  private int width = -1;
  private int height = -1;
  
  private Supplier<Enumeration<BufferedImage>> frames;
  
  public Animation(int width, int height, Supplier<Enumeration<BufferedImage>> frames) {
    if(width < 1 || height < 1) throw new IllegalArgumentException();
    this.width = width;
    this.height = height;
    this.frames = frames;
  }
  
  
  public void writeMKV(String path) throws IOException {
    SequenceEncoder encoder = SequenceEncoder.create30Fps(new File(path));
    
    var e = frames.get();
    while(e.hasMoreElements()) {
      var frame = e.nextElement();
      Picture picture = Picture.create(width, height, ColorSpace.RGB);
      var b = frame.getData().getDataBuffer();
      for(int i=0;i<height;i++) { 
        for(int j=0;j<width;j++) { 
          var p = new Pixel32Bits(b.getElem(((i*width)+j)));
          
          picture.getPlaneData(0)[3*((i*width)+j)] = (byte)(p.getR()-128);
          picture.getPlaneData(0)[3*((i*width)+j)+1] = (byte)(p.getG()-128);
          picture.getPlaneData(0)[3*((i*width)+j)+2] = (byte)(p.getB()-128);
        }
      }
      encoder.encodeNativeFrame(picture);
    }
    
    encoder.finish();
  }
}
