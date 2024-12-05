package name.ncg777.maths.words.relations;

import java.util.BitSet;
import java.util.function.Predicate;

import name.ncg777.maths.relations.Relation;
import name.ncg777.maths.words.BinaryWord;

public class PredicatedXOR implements 
    Relation<BinaryWord, BinaryWord>   {
  
  public PredicatedXOR(Predicate<BinaryWord> pred){
    ld = pred;
  }
    private Predicate<BinaryWord> ld;
  @Override
  public boolean apply(BinaryWord a, BinaryWord b) {
    BitSet o = new BitSet(a.getN());
    o.or(a);
    o.xor(b);
    
    return ld.test(BinaryWord.buildRhythm(o, a.getN()));
  }
}
