package name.ncg.Music.SequencePredicates;

import java.util.Collection;
import java.util.function.Predicate;

import name.ncg.CS.Functional.StandardAndGuavaPredicate;
import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.Rhythm;


public class PredicatedSeqRhythms implements StandardAndGuavaPredicate<Sequence> {
  final private Predicate<Rhythm> pred;

  public PredicatedSeqRhythms(Predicate<Rhythm> pred) {this.pred = pred;}
  @Override
  public boolean apply(Sequence input) {
    Collection<Rhythm> t = input.getRhythms().values();
    
    for(Rhythm r : t) {
      if(!pred.test(r)) return false;
    }
    return true;
  }

}
