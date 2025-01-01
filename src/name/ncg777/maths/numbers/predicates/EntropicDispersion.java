package name.ncg777.maths.numbers.predicates;

import java.util.function.Predicate;

import name.ncg777.maths.fuzzy.valuationFunctions.CombinationDispersion;
import name.ncg777.maths.numbers.BinaryNatural;

public class EntropicDispersion implements Predicate<BinaryNatural> {
  private CombinationDispersion cd = new CombinationDispersion();

  public EntropicDispersion() {}
  
  @Override
  public boolean test(BinaryNatural _input) {
    BinaryNatural input = _input.reverse();
    return cd.apply(input).isEntropic(0.025);
  }

}
