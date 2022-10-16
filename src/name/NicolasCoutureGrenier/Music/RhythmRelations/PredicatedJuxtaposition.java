package name.NicolasCoutureGrenier.Music.RhythmRelations;

import java.util.BitSet;
import java.util.function.Predicate;

import name.NicolasCoutureGrenier.Maths.Relations.Relation;
import name.NicolasCoutureGrenier.Music.Rhythm;


public class PredicatedJuxtaposition implements 
Relation<Rhythm, Rhythm>   {
  
  public PredicatedJuxtaposition(Predicate<Rhythm> pred){
    ld = pred;
  }
  
  private Predicate<Rhythm> ld;
  @Override
  public boolean apply(Rhythm a, Rhythm b) {
    int ns = a.getN()+b.getN();
    BitSet bs = new BitSet(ns);
    for(int i=0;i<a.getN();i++){bs.set(i,a.get(i));}
    for(int i=0;i<b.getN();i++){bs.set(i+a.getN(),b.get(i));}
    Rhythm r = Rhythm.buildRhythm(bs, ns);
    
    return ld.test(r);
  }

 

}
