package name.ncg777.maths.words.relations;

import java.util.BitSet;
import java.util.function.Predicate;

import name.ncg777.maths.objects.WordBinary;
import name.ncg777.maths.relations.Relation;


public class PredicatedJuxtaposition implements 
Relation<WordBinary, WordBinary>   {
  
  public PredicatedJuxtaposition(Predicate<WordBinary> pred){
    ld = pred;
  }
  
  private Predicate<WordBinary> ld;
  @Override
  public boolean apply(WordBinary a, WordBinary b) {
    int ns = a.getN()+b.getN();
    BitSet bs = new BitSet(ns);
    for(int i=0;i<a.getN();i++){bs.set(i,a.get(i));}
    for(int i=0;i<b.getN();i++){bs.set(i+a.getN(),b.get(i));}
    WordBinary r = WordBinary.buildRhythm(bs, ns);
    
    return ld.test(r);
  }

 

}
