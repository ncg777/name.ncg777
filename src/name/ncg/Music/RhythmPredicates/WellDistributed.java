package name.ncg.Music.RhythmPredicates;

import java.util.Map;
import java.util.TreeMap;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Maths.DataStructures.HeterogeneousPair;
import name.ncg.Music.Rhythm16Partition;

import com.google.common.base.Predicate;

public class WellDistributed implements StandardAndGuavaPredicate<Rhythm16Partition>  {

  private static Map<HeterogeneousPair<Integer,Integer>,HeterogeneousPair<Double,Double>> 
    minmax = new TreeMap<HeterogeneousPair<Integer,Integer>,
      HeterogeneousPair<Double,Double>>();
  
  @Override
  public boolean apply(Rhythm16Partition arg0) {
    int m = arg0.getRhythm().getK();
    int n = arg0.getRhythms().size();
    double min = 0.0;
    double max = 0.0;
    double mean = (double)m/(double)n;
    HeterogeneousPair<Integer,Integer> p = HeterogeneousPair.makeHeterogeneousPair(m, n);
    if(minmax.containsKey(p)) {
      min = minmax.get(p).getFirst();
      max = minmax.get(p).getSecond();
    } else {
      
      max = ((n-1)*Math.abs(1-mean) + Math.abs(m-(n-1)-mean))/(double)n;
      
      int div = m/n;
      int rem = m - div*n;
      
      int nbdiv = n-rem;
      int nbdivp1 = n-nbdiv;
      
      min = (nbdiv* Math.abs(div-mean) + nbdivp1*Math.abs(div+1-mean))/(double) n;
      minmax.put(p,HeterogeneousPair.makeHeterogeneousPair(min,max));
    }
    
    double val = 0.0;
    for(int i=0;i<n;i++) {
      val += Math.abs(arg0.getRhythms().get(i).getK() - mean);
    }
    val /= (double)n;
    double normalized;
    if(max-min==0) {
      normalized = 0.5;
    } else{
      normalized = (val - min)/(max-min);
    }
    assert(normalized >=0 && normalized <= 1.0);
    return normalized<0.5;
  }

}
