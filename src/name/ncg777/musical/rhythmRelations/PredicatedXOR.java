package name.ncg777.musical.rhythmRelations;

import java.util.BitSet;
import java.util.function.Predicate;

import name.ncg777.mathematics.relations.Relation;
import name.ncg777.musical.Rhythm;

public class PredicatedXOR implements 
    Relation<Rhythm, Rhythm>   {
  
  public PredicatedXOR(Predicate<Rhythm> pred){
    ld = pred;
  }
    private Predicate<Rhythm> ld;
  @Override
  public boolean apply(Rhythm a, Rhythm b) {
    BitSet o = new BitSet(a.getN());
    o.or(a);
    o.xor(b);
    
    return ld.test(Rhythm.buildRhythm(o, a.getN()));
  }
}
