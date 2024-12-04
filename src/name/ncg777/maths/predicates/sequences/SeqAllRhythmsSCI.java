package name.ncg777.maths.predicates.sequences;

import java.util.Collection;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.objects.Sequence;
import name.ncg777.maths.objects.words.BinaryWord;
import name.ncg777.maths.predicates.words.ShadowContourIsomorphic;

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
