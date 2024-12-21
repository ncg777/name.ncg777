package name.ncg777.maths.numbers.predicates;

import java.util.function.Predicate;

import name.ncg777.maths.fuzzy.valuationFunctions.CombinationDispersion;
import name.ncg777.maths.numbers.BinaryNumber;

public class EntropicDispersion implements Predicate<BinaryNumber> {
  private CombinationDispersion cd = new CombinationDispersion();

  public EntropicDispersion() {}
  
  @Override
  public boolean test(BinaryNumber binaryNumber) {
    return cd.apply(binaryNumber).isEntropic(0.025);
  }

}
