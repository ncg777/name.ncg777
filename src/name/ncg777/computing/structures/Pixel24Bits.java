package name.ncg777.computing.structures;

public class Pixel24Bits implements Comparable<Pixel24Bits> {
  private int R=0;
  private int G=0;
  private int B=0;
  
  public Pixel24Bits(int R, int G, int B) {
    if(R < 0 || R > 255 || G < 0 || G > 255 || B < 0 || B > 255) throw new IllegalArgumentException();
    this.R=R;
    this.G=G;
    this.B=B;
  }
  
  public Pixel24Bits(int V) {
    if(V < 0 || V >= 16777216) throw new IllegalArgumentException();
    this.B = V%256;
    V = (V-this.B)/256;
    this.G = V%256;
    V = (V-this.G)/256;
    this.R = V;
  }
  
  public int toInteger() {
    return this.B+(this.G*256)+this.R*65536;
  }

  public int getR() {return R;}
  public int getG() {return G;}
  public int getB() {return B;}
  
  @Override
  public int compareTo(Pixel24Bits o) {
    return this.toInteger()-o.toInteger();
  }
}
