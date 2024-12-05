package name.ncg777.maths.words.predicates;

import java.util.function.Predicate;

import name.ncg777.maths.fuzzy.valuationFunctions.CombinationDispersion;
import name.ncg777.maths.words.BinaryWord;

public class EntropicDispersion implements Predicate<BinaryWord> {
  private CombinationDispersion cd = new CombinationDispersion();

  public EntropicDispersion() {}
  
  @Override
  public boolean test(BinaryWord binaryWord) {
    return cd.apply(binaryWord).isEntropic(0.025);
  }

}
