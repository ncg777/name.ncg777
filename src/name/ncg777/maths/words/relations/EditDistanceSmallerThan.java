package name.ncg777.maths.words.relations;

import java.util.BitSet;

import name.ncg777.maths.objects.WordBinary;
import name.ncg777.maths.relations.Relation;


public class EditDistanceSmallerThan implements 
Relation<WordBinary, WordBinary>   {
  private int _n = 2;
  public EditDistanceSmallerThan() {}
  public EditDistanceSmallerThan(int n) {
    _n = n;
  }
  @Override
  public boolean apply(WordBinary a, WordBinary b) {
    BitSet t = ((BitSet)a.clone());
    t.xor(b);
    return t.cardinality() < _n;
  }
}
