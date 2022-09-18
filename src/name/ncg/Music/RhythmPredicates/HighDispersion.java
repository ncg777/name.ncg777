package name.ncg.Music.RhythmPredicates;

import name.ncg.Maths.FuzzyLogic.Valuators.CombinationDispersion;
import name.ncg.Music.Rhythm;

import com.google.common.base.Predicate;

public class HighDispersion implements Predicate<Rhythm> {
  private CombinationDispersion cd = new CombinationDispersion();
  
  @Override
  public boolean apply(Rhythm arg0) {
    return cd.apply(arg0).quantize(0.65);
  }

}
