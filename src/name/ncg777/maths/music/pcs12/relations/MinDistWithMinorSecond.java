package name.ncg777.maths.music.pcs12.relations;

import name.ncg777.maths.music.pcs12.Pcs12;
import name.ncg777.maths.relations.Relation;
import name.ncg777.maths.sequences.Sequence;


public class MinDistWithMinorSecond  implements Relation<Pcs12, Pcs12> {
 
  public MinDistWithMinorSecond() {
  }
  
  @Override
  public boolean apply(Pcs12 a, Pcs12 b) {
    if(a.getK() != b.getK()) return false;
    Sequence s = MinDistConsonant.minDistSeq(a.asSequence(), b.asSequence());
    return ((s.contains(1)));
    
  }
  
}