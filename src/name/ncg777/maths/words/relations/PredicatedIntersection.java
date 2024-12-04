package name.ncg777.maths.words.relations;

import java.util.BitSet;
import java.util.function.Predicate;

import name.ncg777.maths.objects.WordBinary;
import name.ncg777.maths.relations.Relation;


public class PredicatedIntersection implements 
Relation<WordBinary, WordBinary>   {
  
  public PredicatedIntersection(Predicate<WordBinary> pred){
    ld = pred;
  }
  
  private Predicate<WordBinary> ld;
  @Override
  public boolean apply(WordBinary a, WordBinary b) {
    BitSet o = new BitSet(a.getN());
    o.or(a);
    o.and(b);
    return ld.test(WordBinary.buildRhythm(o, a.getN()));
  }
}
