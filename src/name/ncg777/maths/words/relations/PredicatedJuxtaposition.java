package name.ncg777.maths.words.relations;

import java.util.BitSet;
import java.util.function.Predicate;

import name.ncg777.maths.relations.Relation;
import name.ncg777.maths.words.BinaryWord;


public class PredicatedJuxtaposition implements 
Relation<BinaryWord, BinaryWord>   {
  
  public PredicatedJuxtaposition(Predicate<BinaryWord> pred){
    ld = pred;
  }
  
  private Predicate<BinaryWord> ld;
  @Override
  public boolean apply(BinaryWord a, BinaryWord b) {
    int ns = a.getN()+b.getN();
    BitSet bs = new BitSet(ns);
    for(int i=0;i<a.getN();i++){bs.set(i,a.get(i));}
    for(int i=0;i<b.getN();i++){bs.set(i+a.getN(),b.get(i));}
    BinaryWord r = BinaryWord.buildRhythm(bs, ns);
    
    return ld.test(r);
  }

 

}
