import java.io.FileNotFoundException;
import java.io.PrintWriter;

import name.ncg.Statistics.RandomNumberGenerator;

public class bubbles {

  public static void main(String[] args) throws FileNotFoundException {
    PrintWriter p = new PrintWriter("d:/bubbles.bat");
    for(int i=0;i<100;i++){
      
      final String smacro = "--smacro:";
      String smacros = "";
      
      smacros += " " + smacro + "dur=" + Double.toString(1.0*RandomNumberGenerator.nextDouble());
      smacros += " " + smacro + "startfreq=" + Double.toString(50*RandomNumberGenerator.nextDouble());
      smacros += " " + smacro + "endfreq=" + Double.toString(10*RandomNumberGenerator.nextDouble());
      smacros += " " + smacro + "carfreq=" + Double.toString(4*RandomNumberGenerator.nextDouble());
      smacros += " " + smacro + "modfreq=" + Double.toString(4*RandomNumberGenerator.nextDouble());
      smacros += " " + smacro + "modindex=" + Double.toString(100*RandomNumberGenerator.nextDouble());
      smacros += " " + smacro + "noiseamp=" + Double.toString(RandomNumberGenerator.nextDouble());
      smacros += " " + smacro + "steeppch=" + Double.toString(1.0+100.0*RandomNumberGenerator.nextDouble());
      smacros += " " + smacro + "steepamp=" + Double.toString(1.0+100.0*RandomNumberGenerator.nextDouble());
      String fname = "bubbles/bubble_" + String.format("%02d", i) + ".wav";
      String csdname = "bubbles.csd";
      String command = "csound " + smacros + " -o " + fname + " " + csdname; 
      p.println(command);
      
    }
    
    p.close();

  }

}
