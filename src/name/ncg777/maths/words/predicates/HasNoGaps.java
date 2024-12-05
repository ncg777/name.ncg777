package name.ncg777.maths.words.predicates;

import java.util.LinkedList;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.words.BinaryWord;

public class HasNoGaps implements StandardAndGuavaPredicate<BinaryWord> {

  public HasNoGaps() {

  }


  @Override
  public boolean apply(@Nonnull BinaryWord input) {
    Sequence s = input.getIntervalVector();
    LinkedList<Integer> l = new LinkedList<Integer>();

    for (int i = 0; i < s.size(); i++) {
      if (s.get(i) != 0) {
        l.add(i);
      }
    }

    if (l.size() == 1) {
      return true;
    }

    for (int i = 0; i < l.size() - 1; i++) {
      if ((l.get(i + 1) - l.get(i)) != 1) {
        return false;
      }
    }
    return true;


  }

}
