package name.ncg777.maths.words.predicates;

import java.util.Set;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.words.BinaryWord;

public class IsInSet implements StandardAndGuavaPredicate<BinaryWord> {
  private Set<BinaryWord> set;
  public IsInSet(Set<BinaryWord> set) {
    this.set = set;
  }
  @Override
  public boolean apply(@Nonnull BinaryWord r) {
    boolean output = set.contains(r);
    return output;
  }

}