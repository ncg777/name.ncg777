package name.ncg777.maths.numbers.relations;


import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.relations.Relation;


public class SamePhase implements 
Relation<BinaryNatural, BinaryNatural>   {
 
  @Override
  public boolean apply(BinaryNatural a, BinaryNatural b) {
    int na = a.nextSetBit(0);
    int nb = b.nextSetBit(0);
    
    if(na == -1 || nb == -1) return false;
    
    return na == nb;
  }
}