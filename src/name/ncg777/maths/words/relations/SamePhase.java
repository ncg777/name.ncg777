package name.ncg777.maths.words.relations;


import name.ncg777.maths.relations.Relation;
import name.ncg777.maths.words.BinaryWord;


public class SamePhase implements 
Relation<BinaryWord, BinaryWord>   {
 
  @Override
  public boolean apply(BinaryWord a, BinaryWord b) {
    int na = a.nextSetBit(0);
    int nb = b.nextSetBit(0);
    
    if(na == -1 || nb == -1) return false;
    
    return na == nb;
  }
}