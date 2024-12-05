package name.ncg777.maths.pitchClassSet12.relations;

import name.ncg777.maths.pitchClassSet12.PitchClassSet12;
import name.ncg777.maths.relations.Relation;
import name.ncg777.maths.sequences.Sequence;


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