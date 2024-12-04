package name.ncg777.maths.words.predicates;

import java.util.Set;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.objects.WordBinary;

public class IsInSet implements StandardAndGuavaPredicate<WordBinary> {
  private Set<WordBinary> set;
  public IsInSet(Set<WordBinary> set) {
    this.set = set;
  }
  @Override
  public boolean apply(@Nonnull WordBinary r) {
    boolean output = set.contains(r);
    return output;
  }

}