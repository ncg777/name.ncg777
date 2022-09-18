package name.ncg.Music.RhythmRelations;

import java.util.BitSet;

import com.google.common.base.Predicate;

import name.ncg.Maths.Relations.Relation;
import name.ncg.Music.Rhythm;


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
    Rhythm r = new Rhythm(bs, ns);
    
    return ld.apply(r);
  }

 

}
