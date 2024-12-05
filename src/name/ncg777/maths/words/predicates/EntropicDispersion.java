package name.ncg777.maths.words.predicates;

import java.util.function.Predicate;

import name.ncg777.maths.fuzzy.valuationFunctions.CombinationDispersion;
import name.ncg777.maths.words.Word;

public class EntropicDispersion implements Predicate<Word> {
  private CombinationDispersion cd = new CombinationDispersion();

  public EntropicDispersion() {
  }
  @Override
  public boolean test(Word arg0) {
    return cd.apply(arg0.toBinaryWord()).isEntropic(0.025);
  }

}
