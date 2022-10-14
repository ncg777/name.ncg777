package name.ncg.Maths.FuzzyLogic.Valuators;

import java.util.TreeMap;

import com.google.common.base.Function;

import name.ncg.Maths.Combination;
import name.ncg.Maths.DataStructures.Interval;
import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Maths.DataStructures.HeterogeneousPair;
import name.ncg.Maths.Enumerations.CombinationEnumeration;
import name.ncg.Maths.Enumerations.CompositionEnumeration;
import name.ncg.Maths.FuzzyLogic.FuzzyVariable;

public class CombinationEntropy implements Function<Combination, FuzzyVariable> {

  private static TreeMap<Integer, Interval<Double>> minmax = new TreeMap<>();

  static {
    /*
     int[] ns = {12,16,24,32,96,192};
    
    for(int n : ns) {
      int k = Double.valueOf(Math.floor((Math.sqrt((double)(1+8*n))-1.0)/2.0)).intValue();
      
      var ce = new CompositionEnumeration(k);
      double max = Double.MIN_VALUE;
      while(ce.hasMoreElements()) {
        Sequence s = ce.nextElement().asSequence();
        double h = 0.0;
        for(int i: s) {
          double p = ((double)i)/((double)k);
          h += p*Math.log(p);
        }
        h = (h==0.0?0.0:-h);
        if(h > max) max = h;
      }
      
      System.out.println("n=" + Integer.toString(n) + ", k=" + Integer.toString(k) + ", Hmax=" + Double.toString(max));
    }
    //    n=12, k=4, Hmax=1.3862943611198906
    //    n=16, k=5, Hmax=1.6094379124341005
    //    n=24, k=6, Hmax=1.7917594692280547
    //    n=32, k=7, Hmax=1.945910149055313
    //    n=96, k=13, Hmax=2.5649493574615376
    //    n=192, k=19, Hmax=2.9444389791664403
    //    n=384, k=27, Hmax=3.2958368660043296
     */

    minmax.put(12, Interval.makeClosedInterval(0.0,1.3862943611198906));
    minmax.put(16, Interval.makeClosedInterval(0.0,1.6094379124341005));
    minmax.put(24, Interval.makeClosedInterval(0.0,1.7917594692280547));
    minmax.put(32, Interval.makeClosedInterval(0.0,1.945910149055313));
    minmax.put(96, Interval.makeClosedInterval(0.0,2.5649493574615376));
    minmax.put(192, Interval.makeClosedInterval(0.0,2.9444389791664403));
    minmax.put(384, Interval.makeClosedInterval(0.0,3.2958368660043296));
  }
  
  @Override
  public FuzzyVariable apply(Combination input) {
    int n = input.getN();
    
    double min = 0.0;
    double max = Double.MIN_VALUE;

    if (!minmax.containsKey(n)) {
      int k = Double.valueOf(Math.floor((Math.sqrt((double)(1+8*n))-1.0)/2.0)).intValue();
      
      var ce = new CompositionEnumeration(k);
      
      while(ce.hasMoreElements()) {
        Sequence s = ce.nextElement().asSequence();
        double h = 0.0;
        for(int i: s) {
          double p = ((double)i)/((double)k);
          h += p*Math.log(p);
        }
        h = (h==0.0?0.0:-h);
        if(h > max) max = h;
      }
      minmax.put(n, Interval.makeClosedInterval(0.0, max));
    } else {
      min = minmax.get(n).getMinimum();
      max = minmax.get(n).getMaximum();
    }
    
    if (min == max) {
      return FuzzyVariable.from(0.5);
    } else {
      return FuzzyVariable.from((input.getComposition().asSequence().entropy() - min) / (max - min));
    }
  }


}
