package name.ncg777.maths.numbers.relations;


import name.ncg777.maths.numbers.BinaryNumber;
import name.ncg777.maths.relations.Relation;


public class SamePhase implements 
Relation<BinaryNumber, BinaryNumber>   {
 
  @Override
  public boolean apply(BinaryNumber a, BinaryNumber b) {
    int na = a.nextSetBit(0);
    int nb = b.nextSetBit(0);
    
    if(na == -1 || nb == -1) return false;
    
    return na == nb;
  }
}