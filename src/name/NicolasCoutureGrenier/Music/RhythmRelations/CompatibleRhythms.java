package name.NicolasCoutureGrenier.Music.RhythmRelations;

import java.util.BitSet;

import name.NicolasCoutureGrenier.Maths.Numbers;
import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;
import name.NicolasCoutureGrenier.Maths.Relations.Relation;
import name.NicolasCoutureGrenier.Music.Rhythm;


public class CompatibleRhythms implements 
Relation<Rhythm, Rhythm>   {
  
  @Override
  public boolean apply(Rhythm a, Rhythm b) {
    int l = a.getN()/4;
    
    for(int i=0;i<4;i++) {
      BitSet ba = new BitSet(l);
      BitSet bb = new BitSet(l);
      for(int j=0;j<l;j++) {
        ba.set(j, a.get((i*l) +j));
        bb.set(j, b.get((i*l) +j));
      }
      if(!sub(Rhythm.buildRhythm(ba,l),Rhythm.buildRhythm(bb,l))) return false;
    }
    return true;
  }
  
  private boolean sub(Rhythm a, Rhythm b) {
    BitSet bs = new BitSet(a.getN());
    bs.or(a);
    bs.or(b);
    Sequence s = Rhythm.buildRhythm(bs, a.getN()).getComposition().asSequence();
    Integer gcd = a.getN();
    
    for(var i : s) {
      gcd = Numbers.gcd(gcd, i);
      if(gcd < 3) return false;
    }
    return true;
  }
}
