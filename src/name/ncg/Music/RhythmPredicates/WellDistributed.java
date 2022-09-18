package name.ncg.Music.RhythmPredicates;

import java.util.Map;
import java.util.TreeMap;

import name.ncg.Maths.DataStructures.OrderedPair;
import name.ncg.Music.Rhythm16Partition;

import com.google.common.base.Predicate;

public class WellDistributed implements Predicate<Rhythm16Partition>  {

  private static Map<OrderedPair<Integer,Integer>,OrderedPair<Double,Double>> 
    minmax = new TreeMap<OrderedPair<Integer,Integer>,
      OrderedPair<Double,Double>>();
  
  @Override
  public boolean apply(Rhythm16Partition arg0) {
    int m = arg0.getRhythm().getK();
    int n = arg0.getRhythms().size();
    double min = 0.0;
    double max = 0.0;
    double mean = (double)m/(double)n;
    OrderedPair<Integer,Integer> p = OrderedPair.makeOrderedPair(m, n);
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
      minmax.put(p,OrderedPair.makeOrderedPair(min,max));
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
