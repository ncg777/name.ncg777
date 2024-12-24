package name.ncg777.computing.structures;

import java.util.ArrayList;
import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.Codec;
import org.jcodec.common.Format;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rational;

import java.io.IOException;

public class Animation {
  private int width = -1;
  private int height = -1;
  
  private ArrayList<Image24Bits> frames = new ArrayList<>();
  
  public Animation(int width, int height) {
    if(width < 1 || height < 1) throw new IllegalArgumentException();
    this.width = width;
    this.height = height;
  }
  public void add(Image24Bits img) {
    if(img.rowCount() != height || img.columnCount() != width) throw new IllegalArgumentException();
    frames.add(img);
  }
  
  public void remove(int i) {
    frames.remove(i);
  }
  
  public void writeAVI(String path) throws IOException {
    var channel = NIOUtils.writableFileChannel(path);

    var fps = Rational.R(30, 1);
    var outputFormat = Format.MKV;
    var outputVideoCodec = Codec.VP9;
    Codec outputAudioCodec = null;

    SequenceEncoder encoder = new SequenceEncoder(channel, fps, outputFormat, outputVideoCodec, outputAudioCodec);

    for (var frame : this.frames) {
      Picture picture = Picture.create(width, height, ColorSpace.RGB);
      for(int i=0;i<height;i++) { 
        for(int j=0;j<width;j++) { 
          picture.getPlaneData(0)[(i*width)+j] = (byte)frame.get(i,j).getR();
          picture.getPlaneData(1)[(i*width)+j] = (byte)frame.get(i,j).getG();
          picture.getPlaneData(2)[(i*width)+j] = (byte)frame.get(i,j).getB();
        }
      }
      encoder.encodeNativeFrame(picture);
    }
    
    encoder.finish();
  }
}
