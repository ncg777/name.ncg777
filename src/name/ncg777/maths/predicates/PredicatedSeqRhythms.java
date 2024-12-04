package name.ncg777.maths.predicates;

import java.util.Collection;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.objects.Sequence;
import name.ncg777.maths.objects.WordBinary;


public class PredicatedSeqRhythms implements StandardAndGuavaPredicate<Sequence> {
  final private Predicate<WordBinary> pred;

  public PredicatedSeqRhythms(Predicate<WordBinary> pred) {this.pred = pred;}
  @Override
  public boolean apply(@Nonnull Sequence input) {
    Collection<WordBinary> t = input.getRhythms().values();
    
    for(WordBinary r : t) {
      if(!pred.test(r)) return false;
    }
    return true;
  }

}
