package name.ncg777.maths.music.pcs12.relations;

import name.ncg777.maths.Numbers;
import name.ncg777.maths.music.pcs12.Pcs12;
import name.ncg777.maths.relations.Relation;
import name.ncg777.maths.sequences.Sequence;


public class MinDistConsonant  implements Relation<Pcs12, Pcs12> {
 
  public MinDistConsonant() {
  }
  
  @Override
  public boolean apply(Pcs12 a, Pcs12 b) {
    if(a.getK() != b.getK()) return false;
    Sequence s = minDistSeq(a.asSequence(), b.asSequence());
    return !s.contains(1);
    
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