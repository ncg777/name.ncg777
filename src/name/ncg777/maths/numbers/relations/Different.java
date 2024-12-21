package name.ncg777.maths.numbers.relations;

import name.ncg777.maths.numbers.BinaryNumber;
import name.ncg777.maths.relations.Relation;


public class Different implements Relation<BinaryNumber, BinaryNumber>  {

  @Override
  public boolean apply(BinaryNumber a, BinaryNumber b) {
    return !a.equals(b);
  }

}
