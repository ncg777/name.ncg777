package name.ncg777.computing.structures;

import java.awt.Color;

public class Pixel32Bits implements Comparable<Pixel32Bits> {
  private int A=0;
  private int R=0;
  private int G=0;
  private int B=0;
  
  public Pixel32Bits(int A, int R, int G, int B) {
    if(A < 0 || A > 255 || R < 0 || R > 255 || G < 0 || G > 255 || B < 0 || B > 255) throw new IllegalArgumentException();
    this.A=A;
    this.R=R;
    this.G=G;
    this.B=B;
  }
  
  public Pixel32Bits(int color) {
    this.A = (color >> 24) & 0xFF;
    this.R = (color >> 16) & 0xFF;
    this.G = (color >> 8) & 0xFF;
    this.B = color & 0xFF;
  }
  
  public Pixel32Bits(Color color) {
    this.A = color.getAlpha();
    this.R = color.getRed();
    this.G = color.getGreen();
    this.B = color.getBlue();
  }
  
  public int toInteger() {
    return (this.A << 24) | (this.R << 16) | (this.G << 8) | this.B;
  }
  
  public int getA() {return A;}
  public int getR() {return R;}
  public int getG() {return G;}
  public int getB() {return B;}
  
  @Override
  public int compareTo(Pixel32Bits o) {
    return this.toInteger()-o.toInteger();
  }
}
