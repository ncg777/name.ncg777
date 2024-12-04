package name.ncg777.music.pitchClassSet12Relations;

import name.ncg777.maths.objects.Sequence;
import name.ncg777.maths.relations.Relation;
import name.ncg777.music.PitchClassSet12;


public class MinDistWithMinorSecond  implements Relation<PitchClassSet12, PitchClassSet12> {
 
  public MinDistWithMinorSecond() {
  }
  
  @Override
  public boolean apply(PitchClassSet12 a, PitchClassSet12 b) {
    if(a.getK() != b.getK()) return false;
    Sequence s = MinDistConsonant.minDistSeq(a.asSequence(), b.asSequence());
    return ((s.contains(1)));
    
  }
  
}