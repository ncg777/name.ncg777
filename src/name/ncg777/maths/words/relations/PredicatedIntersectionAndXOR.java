package name.ncg777.maths.words.relations;

import java.util.BitSet;
import java.util.function.Predicate;

import name.ncg777.maths.objects.words.BinaryWord;
import name.ncg777.maths.relations.Relation;

public class PredicatedIntersectionAndXOR implements 
    Relation<BinaryWord, BinaryWord>   {
  
  public PredicatedIntersectionAndXOR(Predicate<BinaryWord> pred){
    ld = pred;
  }
    private Predicate<BinaryWord> ld;
  @Override
  public boolean apply(BinaryWord a, BinaryWord b) {
    BitSet o = new BitSet(a.getN());
    o.or(a);
    o.and(b);
    
    boolean r1 = ld.test(BinaryWord.buildRhythm(o, a.getN()));
    o = new BitSet(a.getN());
    o.or(a);
    o.xor(b);
    boolean r2 = ld.test(BinaryWord.buildRhythm(o, a.getN()));
    return r1 && r2;
  }
}