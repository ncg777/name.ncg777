package name.ncg777.maths.words.relations;

import java.util.ArrayList;
import java.util.List;

import name.ncg777.maths.objects.WordHexa;
import name.ncg777.maths.relations.Relation;

public class SpectrumProximity implements Relation<WordHexa, WordHexa> {

  private double max = 0;
  public SpectrumProximity(double max) { this.max = max; }
  
  @Override
  public boolean apply(WordHexa a, WordHexa b) {
    List<Double> da = calcDistribution(WordHexa.calcSpectrum(a));
    List<Double> db = calcDistribution(WordHexa.calcSpectrum(b));
    
    double dist = 0.0;
    for(int i=0;i<8;i++) {
      dist += Math.pow(da.get(i)-db.get(i), 2.0);
    }
    dist = Math.sqrt(dist);
    return dist < max;
  }
  
  private static List<Double> calcDistribution(List<Integer> s) {
    int sum = 0;
    for(int i : s) {
      sum += i;
    }
    
    if(sum == 0) sum = 1;
    
    List<Double> distribution = new ArrayList<Double>();
    
    for(int i=0;i<s.size();i++) {
      distribution.add(((double)s.get(i))/((double)sum));
    }
    
    return distribution;
  }
}
