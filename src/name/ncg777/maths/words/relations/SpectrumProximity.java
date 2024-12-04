package name.ncg777.maths.words.relations;

import java.util.ArrayList;
import java.util.List;

import name.ncg777.maths.objects.words.HexadecimalWord;
import name.ncg777.maths.relations.Relation;

public class SpectrumProximity implements Relation<HexadecimalWord, HexadecimalWord> {

  private double max = 0;
  public SpectrumProximity(double max) { this.max = max; }
  
  @Override
  public boolean apply(HexadecimalWord a, HexadecimalWord b) {
    List<Double> da = calcDistribution(HexadecimalWord.calcSpectrum(a));
    List<Double> db = calcDistribution(HexadecimalWord.calcSpectrum(b));
    
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
