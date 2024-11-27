package name.ncg777.musical.pitchClassSet12Relations;

import name.ncg777.mathematics.objects.Sequence;
import name.ncg777.mathematics.relations.Relation;
import name.ncg777.musical.pitchClassSet12;


public class MinDistWithMinorSecond  implements Relation<pitchClassSet12, pitchClassSet12> {
 
  public MinDistWithMinorSecond() {
  }
  
  @Override
  public boolean apply(pitchClassSet12 a, pitchClassSet12 b) {
    if(a.getK() != b.getK()) return false;
    Sequence s = MinDistConsonant.minDistSeq(a.asSequence(), b.asSequence());
    return ((s.contains(1)));
    
  }
  
}