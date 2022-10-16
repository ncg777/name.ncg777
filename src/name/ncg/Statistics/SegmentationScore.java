package name.ncg.Statistics;

import name.ncg.Maths.DataStructures.Composition;
import name.ncg.Maths.DataStructures.Sequence;

public class SegmentationScore {

  /**
   * To segment composition c using composition p yields the following score.
   * 
   * d_i for each block := The sum of distances to the mean of each block of c 
   *    as defined by p and divided by size of block 
   * d := The sum of distances to the mean of the whole of c and divided by
   *    the size of c.
   * 
   * factor = Math.log10((factor*9.0)+1.0); the score is factor * (d - sum(d_i))/d
   * 
   * //TODO name that score or find it somewhere
   * 
   * The idea is to use backtracking with refinements to find the composition p such that p
   * maximizes this score for c, starting from p being the empty composition (score = 0 as long as
   * d!=0)
   * 
   * @see Composition#refinements()
   * 
   *      The number of blocks is minimized.
   *      
   *      It is not necessarily the case that sum(d_i) <= d
   *      A counter example can be generated randomly, for example :
   *        c := (1 2 6 2 1 1 2 1 1) and
   *        p := (2 7) or p := (6 3)
   * 
   *      If d == 0, factor is returned since d - sum(d_i) = 0 and d = 0, and I 
   *      assume factor*(0/0) simplifies to factor
   * 
   * @param c
   * @param p
   * @return
   */
  public static Double evaluate(double[] c, Composition p) {
    if (p.getTotal() != c.length) {
      throw new IllegalArgumentException();
    }
    SumOfDistanceToMean mad = new SumOfDistanceToMean();
    double totalMad = mad.evaluate(c);

    Sequence ps = p.asSequence();

    int nbBlocks = ps.size();
    int offset = 0;
    double o = 0.0;
    for (int i = 0; i < nbBlocks; i++) {
      o += mad.evaluate(c, offset, ps.get(i));
      offset += ps.get(i);
    }

    int n = c.length;
    int k = ps.size(); // 1 <= k <= n
    double factor = ((double) (n - k)) / ((double) (n - 1)); // 0 <= factor <= 1

    factor = Math.log10((factor * 9.0) + 1.0); // 0 <= factor <= 1

    if (totalMad == 0.0) {
      return factor;
    }
    
    
    o = factor * ((totalMad - o) / totalMad);
    
    return o;
  }
}
