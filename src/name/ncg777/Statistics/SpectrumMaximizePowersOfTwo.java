package name.ncg777.Statistics;

import java.util.ArrayList;
import java.util.List;

import name.ncg777.Maths.Numbers;

public class SpectrumMaximizePowersOfTwo {
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
      acc += distribution.get(i)*(Math.log(i+1)/Math.log(2.0))*(Numbers.isPowerOfTwo(i+1)?1.0:-1.0);
    }
    return acc;
    
  }
  
}
