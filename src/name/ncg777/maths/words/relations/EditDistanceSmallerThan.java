package name.ncg777.maths.words.relations;

import java.util.BitSet;

import name.ncg777.maths.objects.words.BinaryWord;
import name.ncg777.maths.relations.Relation;


public class EditDistanceSmallerThan implements 
Relation<BinaryWord, BinaryWord>   {
  private int _n = 2;
  public EditDistanceSmallerThan() {}
  public EditDistanceSmallerThan(int n) {
    _n = n;
  }
  @Override
  public boolean apply(BinaryWord a, BinaryWord b) {
    BitSet t = ((BitSet)a.clone());
    t.xor(b);
    return t.cardinality() < _n;
  }
}
