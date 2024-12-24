package name.ncg777.maths.sequences.predicates;

import java.util.Collection;
import java.util.function.Predicate;

import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.numbers.predicates.ShadowContourIsomorphic;
import name.ncg777.maths.sequences.Sequence;

public class SeqAllRhythmsSCI implements Predicate<Sequence> {
  ShadowContourIsomorphic sci;
  
  public SeqAllRhythmsSCI() {
    sci = new ShadowContourIsomorphic();
  }
  
  @Override
  public boolean test(Sequence input) {
    Collection<BinaryNatural> words = input.getBinaryWords().values();
    
    for(BinaryNatural r : words) {
      if(!sci.apply(r)) return false;
    }
    return true;
  }
}