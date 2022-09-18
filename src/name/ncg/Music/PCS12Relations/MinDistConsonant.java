package name.ncg.Music.PCS12Relations;

import name.ncg.Maths.Numbers;
import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Maths.Relations.Relation;
import name.ncg.Music.PCS12;


public class MinDistConsonant  implements Relation<PCS12, PCS12> {
 
  public MinDistConsonant() {
  }
  
  @Override
  public boolean apply(PCS12 a, PCS12 b) {
    if(a.getK() != b.getK()) return false;
    Sequence s = minDistSeq(a.asSequence(), b.asSequence());
    return ((!s.contains(1) || !s.contains(2)) && !s.contains(6));
    
  }
  
  public static Sequence minDistSeq(Sequence a, Sequence b) {
    if(a.size() != b.size()) throw new RuntimeException("minDistSeq Sizes don't match.");
    int k = a.size();
    int minDist = Integer.MAX_VALUE;
    
    Sequence o = null;
    
    for(int i=0;i<k;i++) {
      int l = 0;
      Sequence sb = b.rotate(i);
      Sequence d = new Sequence();
      for(int j=0;j<k;j++) {
        int dist = Numbers.minDistMod12(a.get(j), sb.get(j));
        d.add(dist);
        l += dist;
      }
      if(l < minDist) {
        minDist = l;
        o = d;
      }
    }
    return o;
    
  }
}