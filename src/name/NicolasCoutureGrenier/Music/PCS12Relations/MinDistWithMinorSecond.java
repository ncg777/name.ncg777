package name.NicolasCoutureGrenier.Music.PCS12Relations;

import name.NicolasCoutureGrenier.Maths.Objects.Sequence;
import name.NicolasCoutureGrenier.Maths.Relations.Relation;
import name.NicolasCoutureGrenier.Music.PCS12;


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