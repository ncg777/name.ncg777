package name.ncg777.maths.words.relations;

import java.util.BitSet;
import java.util.function.Predicate;

import name.ncg777.maths.objects.words.BinaryWord;
import name.ncg777.maths.relations.Relation;


public class PredicatedUnion implements 
Relation<BinaryWord, BinaryWord>   {
  
  public PredicatedUnion(Predicate<BinaryWord> pred){
    ld = pred;
  }
  
  private Predicate<BinaryWord> ld;
  @Override
  public boolean apply(BinaryWord a, BinaryWord b) {
    BitSet o = new BitSet(a.getN());
    o.or(a);
    o.or(b);
    return ld.test(BinaryWord.buildRhythm(o, a.getN()));
  }
}
