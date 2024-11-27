package name.ncg777.musical.rhythmPredicates;

import java.util.LinkedList;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.mathematics.objects.Sequence;
import name.ncg777.musical.Rhythm;

public class HasNoGaps implements StandardAndGuavaPredicate<Rhythm> {

  public HasNoGaps() {

  }


  @Override
  public boolean apply(@Nonnull Rhythm input) {
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
