package name.ncg.Maths.FuzzyLogic.Valuators;

import java.util.TreeMap;

import com.google.common.base.Function;

import name.ncg.Maths.Combination;
import name.ncg.Maths.DataStructures.Interval;
import name.ncg.Maths.DataStructures.HeterogeneousPair;
import name.ncg.Maths.Enumerations.CombinationEnumeration;
import name.ncg.Maths.FuzzyLogic.FuzzyVariable;

public class CombinationEntropy implements Function<Combination, FuzzyVariable> {

  private static TreeMap<HeterogeneousPair<Integer, Integer>, Interval<Double>> minmax = new TreeMap<>();

  static {
    /*
    Double mins[] = new Double[32];
    Double maxs[] = new Double[32];
    
    for(int i=0;i<32;i++) {
      mins[i] = Double.MAX_VALUE;
      maxs[i] = Double.MIN_VALUE;
    }
    
    int counter = 0;
    CompositionEnumeration ce = new CompositionEnumeration(32);
    while(ce.hasMoreElements()) {
      Sequence comp = ce.nextElement().asSequence();
      double e = comp.entropy();
      if(e<mins[comp.size()-1]) mins[comp.size()-1] = e;
      if(e>maxs[comp.size()-1]) maxs[comp.size()-1] = e;
      
      System.out.println("" + String.format("%.04f", 100.0 * ((double)++counter / 2147483648.0)) + "%");
    }
    
    PrintWriter p = new PrintWriter("d:/minmaxentropy32.txt");
    p.println("k, min, max");
    for(int i=0;i<32;i++) {
      p.println("" + (i+1) + ", " +  mins[i] + ", " + maxs[i]);
    }
    
    p.flush();
    p.close();
    */
    
    /*
    Double mins[] = new Double[24];
    Double maxs[] = new Double[24];
    
    for(int i=0;i<24;i++) {
      mins[i] = Double.MAX_VALUE;
      maxs[i] = Double.MIN_VALUE;
    }
    
    int counter = 0;
    CompositionEnumeration ce = new CompositionEnumeration(24);
    while(ce.hasMoreElements()) {
      Sequence comp = ce.nextElement().asSequence();
      double e = comp.entropy();
      if(e<mins[comp.size()-1]) mins[comp.size()-1] = e;
      if(e>maxs[comp.size()-1]) maxs[comp.size()-1] = e;
      
      System.out.println("" + String.format("%.06f", 100.0 * ((double)++counter / 8388608.0)) + "%");
    }
    
    PrintWriter p = new PrintWriter("d:/minmaxentropy24.txt");
    p.println("k, min, max");
    for(int i=0;i<24;i++) {
      p.println("" + (i+1) + ", " +  mins[i] + ", " + maxs[i]);
    }
    
    p.flush();
    p.close();
    */
    
    // Pre-computed bounds for n=12
    minmax.put(HeterogeneousPair.makeOrderedPair(12, 1),  Interval.makeClosedInterval(0.0,0.0));
    minmax.put(HeterogeneousPair.makeOrderedPair(12, 2),  Interval.makeClosedInterval(0.0,0.6931471805599453));
    minmax.put(HeterogeneousPair.makeOrderedPair(12, 3),  Interval.makeClosedInterval(0.0,1.0986122886681096));
    minmax.put(HeterogeneousPair.makeOrderedPair(12, 4),  Interval.makeClosedInterval(0.0,1.3862943611198906));
    minmax.put(HeterogeneousPair.makeOrderedPair(12, 5),  Interval.makeClosedInterval(0.5004024235381879,1.3321790402101223));
    minmax.put(HeterogeneousPair.makeOrderedPair(12, 6),  Interval.makeClosedInterval(0.0,1.242453324894));
    minmax.put(HeterogeneousPair.makeOrderedPair(12, 7),  Interval.makeClosedInterval(0.410116318288409,1.0042424730540764));
    minmax.put(HeterogeneousPair.makeOrderedPair(12, 8),  Interval.makeClosedInterval(0.37677016125643675,0.9002560512685369));
    minmax.put(HeterogeneousPair.makeOrderedPair(12, 9),  Interval.makeClosedInterval(0.34883209584303193,0.6837389058487535));
    minmax.put(HeterogeneousPair.makeOrderedPair(12, 10),  Interval.makeClosedInterval(0.3250829733914482,0.5004024235381879));
    minmax.put(HeterogeneousPair.makeOrderedPair(12, 11),  Interval.makeClosedInterval(0.30463609734923813,0.30463609734923813));
    minmax.put(HeterogeneousPair.makeOrderedPair(12, 12),  Interval.makeClosedInterval(0.0,0.0));
    
    // Pre-computed bounds for n=16
    minmax.put(HeterogeneousPair.makeOrderedPair(16, 1),  Interval.makeClosedInterval(0.0,0.0));
    minmax.put(HeterogeneousPair.makeOrderedPair(16, 2),  Interval.makeClosedInterval(0.0,0.6931471805599453));
    minmax.put(HeterogeneousPair.makeOrderedPair(16, 3),  Interval.makeClosedInterval(0.6365141682948128,1.0986122886681096));
    minmax.put(HeterogeneousPair.makeOrderedPair(16, 4),  Interval.makeClosedInterval(0.0,1.3862943611198906));
    minmax.put(HeterogeneousPair.makeOrderedPair(16, 5),  Interval.makeClosedInterval(0.5004024235381879,1.6094379124341005));
    minmax.put(HeterogeneousPair.makeOrderedPair(16, 6),  Interval.makeClosedInterval(0.45056120886630463,1.5607104090414063));
    minmax.put(HeterogeneousPair.makeOrderedPair(16, 7),  Interval.makeClosedInterval(0.410116318288409,1.351783994289646));
    minmax.put(HeterogeneousPair.makeOrderedPair(16, 8),  Interval.makeClosedInterval(0.0,1.2554823251787537));
    minmax.put(HeterogeneousPair.makeOrderedPair(16, 9),  Interval.makeClosedInterval(0.34883209584303193,1.1490596969706204));
    minmax.put(HeterogeneousPair.makeOrderedPair(16, 10),  Interval.makeClosedInterval(0.3250829733914482,0.9502705392332347));
    minmax.put(HeterogeneousPair.makeOrderedPair(16, 11),  Interval.makeClosedInterval(0.30463609734923813,0.8599672810355049));
    minmax.put(HeterogeneousPair.makeOrderedPair(16, 12),  Interval.makeClosedInterval(0.2868359830561607,0.7214636866925115));
    minmax.put(HeterogeneousPair.makeOrderedPair(16, 13),  Interval.makeClosedInterval(0.2711893730418441,0.5402041423888608));
    minmax.put(HeterogeneousPair.makeOrderedPair(16, 14),  Interval.makeClosedInterval(0.25731864054383163,0.410116318288409));
    minmax.put(HeterogeneousPair.makeOrderedPair(16, 15),  Interval.makeClosedInterval(0.24493002679463532,0.24493002679463532));
    minmax.put(HeterogeneousPair.makeOrderedPair(16, 16),  Interval.makeClosedInterval(0.0,0.0));

    
    // Pre-computed bounds for n=24
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 1),  Interval.makeClosedInterval(0.0,0.0));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 2),  Interval.makeClosedInterval(0.0,0.6931471805599453));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 3),  Interval.makeClosedInterval(0.0,1.0986122886681096));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 4),  Interval.makeClosedInterval(0.0,1.3862943611198906));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 5),  Interval.makeClosedInterval(0.5004024235381879,1.6094379124341005));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 6),  Interval.makeClosedInterval(0.0,1.7917594692280547));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 7),  Interval.makeClosedInterval(0.410116318288409,1.7478680974667573));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 8),  Interval.makeClosedInterval(0.0,1.7328679513998633));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 9),  Interval.makeClosedInterval(0.34883209584303193,1.5810937501718239));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 10),  Interval.makeClosedInterval(0.3250829733914482,1.5047882836811908));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 11),  Interval.makeClosedInterval(0.30463609734923813,1.4142790651247086));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 12),  Interval.makeClosedInterval(0.0,1.3143738430069454));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 13),  Interval.makeClosedInterval(0.2711893730418441,1.2047933096947843));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 14),  Interval.makeClosedInterval(0.25731864054383163,1.1163670752893367));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 15),  Interval.makeClosedInterval(0.24493002679463532,1.020036958401841));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 16),  Interval.makeClosedInterval(0.2337916587064593,0.918045918065631));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 17),  Interval.makeClosedInterval(0.22371807606583377,0.8082699580001821));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 18),  Interval.makeClosedInterval(0.2145591551764051,0.7298429194806347));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 19),  Interval.makeClosedInterval(0.20619205063323187,0.6330395116644678));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 20),  Interval.makeClosedInterval(0.1985152433458726,0.5181862130502128));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 21),  Interval.makeClosedInterval(0.19144408195771734,0.410116318288409));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 22),  Interval.makeClosedInterval(0.18490739916777568,0.30463609734923813));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 23),  Interval.makeClosedInterval(0.17884491271684755,0.17884491271684755));
    minmax.put(HeterogeneousPair.makeOrderedPair(24, 24),  Interval.makeClosedInterval(0.0,0.0));
    
    // Pre-computed bounds for n=32
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 1),  Interval.makeClosedInterval(0.0,0.0));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 2),  Interval.makeClosedInterval(0.0,0.6931471805599453));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 3),  Interval.makeClosedInterval(0.6365141682948128,1.0986122886681096));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 4),  Interval.makeClosedInterval(0.0,1.3862943611198906));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 5),  Interval.makeClosedInterval(0.5004024235381879,1.6094379124341005));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 6),  Interval.makeClosedInterval(0.45056120886630463,1.7917594692280547));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 7),  Interval.makeClosedInterval(0.410116318288409,1.945910149055313));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 8),  Interval.makeClosedInterval(0.0,1.9061547465398496));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 9),  Interval.makeClosedInterval(0.34883209584303193,1.8891591637540217));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 10),  Interval.makeClosedInterval(0.3250829733914482,1.8343719702816235));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 11),  Interval.makeClosedInterval(0.30463609734923813,1.7201934592198251));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 12),  Interval.makeClosedInterval(0.2868359830561607,1.6326309271543522));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 13),  Interval.makeClosedInterval(0.2711893730418441,1.5857708352080968));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 14),  Interval.makeClosedInterval(0.25731864054383163,1.475076311054695));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 15),  Interval.makeClosedInterval(0.24493002679463532,1.4019454593787286));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 16),  Interval.makeClosedInterval(0.0,1.331660286822432));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 17),  Interval.makeClosedInterval(0.22371807606583377,1.2622431674900145));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 18),  Interval.makeClosedInterval(0.2145591551764051,1.1648729119013703));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 19),  Interval.makeClosedInterval(0.20619205063323187,1.0937704472491416));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 20),  Interval.makeClosedInterval(0.1985152433458726,1.033114087516671));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 21),  Interval.makeClosedInterval(0.19144408195771734,0.9550807986656575));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 22),  Interval.makeClosedInterval(0.18490739916777568,0.8788632452354226));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 23),  Interval.makeClosedInterval(0.17884491271684755,0.8002853336058786));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 24),  Interval.makeClosedInterval(0.1732052067491771,0.7232545110011122));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 25),  Interval.makeClosedInterval(0.167944147734173,0.6592146182148859));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 26),  Interval.makeClosedInterval(0.16302362949436594,0.5857831289187342));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 27),  Interval.makeClosedInterval(0.15841057013179086,0.5027922796200783));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 28),  Interval.makeClosedInterval(0.15407610367102942,0.410116318288409));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 29),  Interval.makeClosedInterval(0.14999492361041503,0.3325942143118925));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 30),  Interval.makeClosedInterval(0.1461447460085638,0.24493002679463532));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 31),  Interval.makeClosedInterval(0.1425058673927378,0.1425058673927378));
    minmax.put(HeterogeneousPair.makeOrderedPair(32, 32),  Interval.makeClosedInterval(0.0,0.0));
  }
  
  @Override
  public FuzzyVariable apply(Combination input) {
    int n = input.getN();
    int k = input.getK();
    HeterogeneousPair<Integer, Integer> nk = HeterogeneousPair.makeOrderedPair(n, k);

    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;

    if (!minmax.containsKey(nk)) {
      CombinationEnumeration ce = new CombinationEnumeration(n, k);
      while(ce.hasMoreElements()) {
        double e = ce.nextElement().getComposition().asSequence().entropy();
        if(e>max) max = e;
        if(e<min) min = e;
      }
      minmax.put(nk, Interval.makeClosedInterval(min, max));
    } else {
      min = minmax.get(nk).getMinimum();
      max = minmax.get(nk).getMaximum();
    }
    
    if (min == max) {
      return FuzzyVariable.from(0.5);
    } else {
      return FuzzyVariable.from((input.getComposition().asSequence().entropy() - min) / (max - min));
    }
  }


}
