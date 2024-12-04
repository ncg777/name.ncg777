package name.ncg777.music.pitchClassSet12.relations;

import name.ncg777.maths.Numbers;
import name.ncg777.maths.objects.Sequence;
import name.ncg777.maths.relations.Relation;
import name.ncg777.music.pitchClassSet12.PitchClassSet12;


public class MinDistConsonant  implements Relation<PitchClassSet12, PitchClassSet12> {
 
  public MinDistConsonant() {
  }
  
  @Override
  public boolean apply(PitchClassSet12 a, PitchClassSet12 b) {
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