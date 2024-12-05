package name.ncg777.maths.sequences.predicates;

import java.util.Collection;
import java.util.function.Predicate;

import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.words.Word;
import name.ncg777.maths.words.predicates.ShadowContourIsomorphic;

public class SeqAllRhythmsSCI implements Predicate<Sequence> {
  ShadowContourIsomorphic sci;
  
  public SeqAllRhythmsSCI() {
    sci = new ShadowContourIsomorphic();
  }
  
  @Override
  public boolean test(Sequence input) {
    Collection<Word> words = input.getBinaryWords().values();
    
    for(Word r : words) {
      if(!sci.apply(r)) return false;
    }
    return true;
  }
}