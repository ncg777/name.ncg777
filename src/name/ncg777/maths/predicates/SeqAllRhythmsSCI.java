package name.ncg777.maths.predicates;

import java.util.Collection;

import javax.annotation.Nonnull;

import name.ncg777.computerScience.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.objects.Sequence;
import name.ncg777.maths.objects.WordBinary;
import name.ncg777.maths.words.predicates.ShadowContourIsomorphic;

public class SeqAllRhythmsSCI implements StandardAndGuavaPredicate<Sequence> {
  ShadowContourIsomorphic sci = new ShadowContourIsomorphic();
  
  @Override
  public boolean apply(@Nonnull Sequence input) {
    Collection<WordBinary> wordBinaries = input.getRhythms().values();
    
    for(WordBinary r : wordBinaries) {
      if(!sci.apply(r)) return false;
    }
    return true;
  }
}
