import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import name.ncg.Statistics.RandomNumberGenerator;

public class random_skip_player {

  public static void main(String[] args) throws FileNotFoundException {
    int n = 1000;
    PrintWriter pw = new PrintWriter("d:/rambogauthier.txt");
    
    
    ArrayList<Double> a = new ArrayList<Double>();
    double offset = 2.5;
    double amplitude_pc = 0.2;
    for(int i=0;i<n;i++){a.add(offset+(-0.5+RandomNumberGenerator.nextDouble())*amplitude_pc*offset);}
    Double acc = a.get(0)/2.0;
    for(int i=1;i<n-1;i++){
      double last_r = a.get(i-1);
      double r = a.get(i);
      double next_r = a.get(i+1);
      pw.println(
        String.format("i   1   %.8f   %.8f  %.8f  $W  [$LEN*%.8f] %.8f %.8f", 
        acc,r,0.4*(RandomNumberGenerator.nextDouble()-0.5)-1+RandomNumberGenerator.nextInt(2)*2,
        RandomNumberGenerator.nextDouble(),
        (last_r/(2.0*r)), (next_r/(2.0*r))));
      
      acc+=r/2.0;
    }
    pw.close();
  }

}
