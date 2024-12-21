package name.ncg777.maths.sequences.predicates;

import java.util.Collection;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.numbers.BinaryNumber;
import name.ncg777.maths.sequences.Sequence;

public class PredicatedSequenceAsBinaryWords implements StandardAndGuavaPredicate<Sequence> {
  final private Predicate<BinaryNumber> pred;

  public PredicatedSequenceAsBinaryWords(Predicate<BinaryNumber> pred) {this.pred = pred;}
  @Override
  public boolean apply(@Nonnull Sequence input) {
    Collection<BinaryNumber> t = input.getBinaryWords().values();
    
    for(BinaryNumber r : t) {
      if(!pred.test(r)) return false;
    }
    return true;
  }
}