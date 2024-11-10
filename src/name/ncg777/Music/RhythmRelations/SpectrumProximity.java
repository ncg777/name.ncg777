package name.ncg777.Music.RhythmRelations;

import java.util.ArrayList;
import java.util.List;

import name.ncg777.Maths.Relations.Relation;
import name.ncg777.Music.Rhythm16;

public class SpectrumProximity implements Relation<Rhythm16, Rhythm16> {

  private double max = 0;
  public SpectrumProximity(double max) { this.max = max; }
  
  @Override
  public boolean apply(Rhythm16 a, Rhythm16 b) {
    List<Double> da = calcDistribution(Rhythm16.calcSpectrum(a));
    List<Double> db = calcDistribution(Rhythm16.calcSpectrum(b));
    
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
