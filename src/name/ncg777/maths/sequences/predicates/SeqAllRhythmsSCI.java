package name.ncg777.maths.sequences.predicates;

import java.util.Collection;

import javax.annotation.Nonnull;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.words.BinaryWord;
import name.ncg777.maths.words.predicates.ShadowContourIsomorphic;

public class SeqAllRhythmsSCI implements StandardAndGuavaPredicate<Sequence> {
  ShadowContourIsomorphic sci = new ShadowContourIsomorphic();
  
  @Override
  public boolean apply(@Nonnull Sequence input) {
    Collection<BinaryWord> binaryWords = input.getRhythms().values();
    
    for(BinaryWord r : binaryWords) {
      if(!sci.apply(r)) return false;
    }
    return true;
  }
}
