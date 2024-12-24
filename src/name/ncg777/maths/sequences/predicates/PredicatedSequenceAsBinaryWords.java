package name.ncg777.maths.sequences.predicates;

import java.util.Collection;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.sequences.Sequence;

public class PredicatedSequenceAsBinaryWords implements StandardAndGuavaPredicate<Sequence> {
  final private Predicate<BinaryNatural> pred;

  public PredicatedSequenceAsBinaryWords(Predicate<BinaryNatural> pred) {this.pred = pred;}
  @Override
  public boolean apply(@Nonnull Sequence input) {
    Collection<BinaryNatural> t = input.getBinaryWords().values();
    
    for(BinaryNatural r : t) {
      if(!pred.test(r)) return false;
    }
    return true;
  }
}