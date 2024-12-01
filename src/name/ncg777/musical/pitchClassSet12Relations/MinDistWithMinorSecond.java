package name.ncg777.musical.pitchClassSet12Relations;

import name.ncg777.mathematics.objects.Sequence;
import name.ncg777.mathematics.relations.Relation;
import name.ncg777.musical.PitchClassSet12;


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