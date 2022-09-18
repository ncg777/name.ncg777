import java.io.FileNotFoundException;
import java.io.PrintWriter;

import name.ncg.Statistics.RandomNumberGenerator;

public class strat_pulse {

  public static void main(String[] args) throws FileNotFoundException {
    PrintWriter p = new PrintWriter("d:/sp short.bat");
    for(int i=0;i<200;i++){
      
      final String smacro = "--smacro:";
      String smacros = "";
      smacros += " " + smacro + "freq=" + Double.toString(0.1*Math.pow(5, 4.0*RandomNumberGenerator.nextDouble()));
      smacros += " " + smacro + "length=" +Double.toString(4.0*RandomNumberGenerator.nextDouble());
      smacros += " " + smacro + "n=" +Integer.toString(10 + RandomNumberGenerator.nextInt(42));
      smacros += " " + smacro + "amp=" +Integer.toString(80);
      smacros += " " + smacro + "wave=" + Integer.toString(1+RandomNumberGenerator.nextInt(3));
      smacros += " " + smacro + "offset=" +Double.toString(2.0 + RandomNumberGenerator.nextDouble()*4);
      smacros += " " + smacro + "index=" +Integer.toString(5 + RandomNumberGenerator.nextInt(95));
      smacros += " " + smacro + "carrier_freq=" + Double.toString(2.0*Math.pow(27.5, RandomNumberGenerator.nextDouble()+1.0));
      smacros += " " + smacro + "mod_index=" + Double.toString(5 + RandomNumberGenerator.nextDouble()*95);
      smacros += " " + smacro + "fm_wave=" + Integer.toString(1+RandomNumberGenerator.nextInt(4));
      String fname = "snd/snd_" + String.format("%02d", i) + ".wav";
      String csdname = "strat_pulse_fm.csd";
      String command = "csound " + smacros + " -o " + fname + " " + csdname; 
      p.println(command);
      
    }
    
    p.close();

  }

}
