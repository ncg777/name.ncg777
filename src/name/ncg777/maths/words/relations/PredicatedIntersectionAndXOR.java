package name.ncg777.maths.words.relations;

import java.util.BitSet;
import java.util.function.Predicate;

import name.ncg777.maths.objects.WordBinary;
import name.ncg777.maths.relations.Relation;

public class PredicatedIntersectionAndXOR implements 
    Relation<WordBinary, WordBinary>   {
  
  public PredicatedIntersectionAndXOR(Predicate<WordBinary> pred){
    ld = pred;
  }
    private Predicate<WordBinary> ld;
  @Override
  public boolean apply(WordBinary a, WordBinary b) {
    BitSet o = new BitSet(a.getN());
    o.or(a);
    o.and(b);
    
    boolean r1 = ld.test(WordBinary.buildRhythm(o, a.getN()));
    o = new BitSet(a.getN());
    o.or(a);
    o.xor(b);
    boolean r2 = ld.test(WordBinary.buildRhythm(o, a.getN()));
    return r1 && r2;
  }
}