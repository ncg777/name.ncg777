package name.ncg.Statistics;

import java.util.ArrayList;
import java.util.List;

public class SpectrumMaximizeQuality {
  public static Double evaluate(List<Integer> s) {
    
    int sum = 0;
    for(int i : s) {
      sum += i;
    }
    
    if(sum == 0) return 0.0;
    
    List<Double> distribution = new ArrayList<Double>();
    
    for(int i=0;i<s.size();i++) {
      distribution.add(((double)s.get(i))/((double)sum));
    }
    
    double acc = 0.0;
    for(int i=0;i<s.size();i++) {
      acc += distribution.get(i)*(Math.log(i+1)/Math.log(2.0))*(Weight(i+1));
    }
    return acc;
    
  }
  private static Double Weight(int i) {
    return ((i%2)==0)?-1.0:1.0;
  }
}
