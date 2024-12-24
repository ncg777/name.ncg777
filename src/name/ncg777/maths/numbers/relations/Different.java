package name.ncg777.maths.numbers.relations;

import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.relations.Relation;


public class Different implements Relation<BinaryNatural, BinaryNatural>  {

  @Override
  public boolean apply(BinaryNatural a, BinaryNatural b) {
    return !a.equals(b);
  }

}
