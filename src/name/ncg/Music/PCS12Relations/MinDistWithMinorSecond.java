package name.ncg.Music.PCS12Relations;

import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Maths.Relations.Relation;
import name.ncg.Music.PCS12;


public class MinDistWithMinorSecond  implements Relation<PCS12, PCS12> {
 
  public MinDistWithMinorSecond() {
  }
  
  @Override
  public boolean apply(PCS12 a, PCS12 b) {
    if(a.getK() != b.getK()) return false;
    Sequence s = MinDistConsonant.minDistSeq(a.asSequence(), b.asSequence());
    return ((s.contains(1)));
    
  }
  
}