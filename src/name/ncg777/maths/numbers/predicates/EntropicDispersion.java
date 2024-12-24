package name.ncg777.maths.numbers.predicates;

import java.util.function.Predicate;

import name.ncg777.maths.fuzzy.valuationFunctions.CombinationDispersion;
import name.ncg777.maths.numbers.BinaryNatural;

public class EntropicDispersion implements Predicate<BinaryNatural> {
  private CombinationDispersion cd = new CombinationDispersion();

  public EntropicDispersion() {}
  
  @Override
  public boolean test(BinaryNatural binaryNatural) {
    return cd.apply(binaryNatural).isEntropic(0.025);
  }

}
