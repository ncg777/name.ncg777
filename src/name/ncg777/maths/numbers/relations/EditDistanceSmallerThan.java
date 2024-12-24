package name.ncg777.maths.numbers.relations;

import java.util.BitSet;

import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.relations.Relation;


public class EditDistanceSmallerThan implements 
Relation<BinaryNatural, BinaryNatural>   {
  private int _n = 2;
  public EditDistanceSmallerThan() {}
  public EditDistanceSmallerThan(int n) {
    _n = n;
  }
  @Override
  public boolean apply(BinaryNatural a, BinaryNatural b) {
    BitSet t = ((BitSet)a.clone());
    t.xor(b);
    return t.cardinality() < _n;
  }
}
