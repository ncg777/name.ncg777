package name.ncg777.maths.sequences.predicates;

import java.util.Collection;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.words.BinaryWord;


public class PredicatedSeqRhythms implements StandardAndGuavaPredicate<Sequence> {
  final private Predicate<BinaryWord> pred;

  public PredicatedSeqRhythms(Predicate<BinaryWord> pred) {this.pred = pred;}
  @Override
  public boolean apply(@Nonnull Sequence input) {
    Collection<BinaryWord> t = input.getRhythms().values();
    
    for(BinaryWord r : t) {
      if(!pred.test(r)) return false;
    }
    return true;
  }

}
