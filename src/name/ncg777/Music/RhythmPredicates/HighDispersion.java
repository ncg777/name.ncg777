package name.ncg777.Music.RhythmPredicates;

import javax.annotation.Nonnull;

import name.ncg777.CS.Functional.StandardAndGuavaPredicate;
import name.ncg777.Maths.FuzzyLogic.Valuators.CombinationDispersion;
import name.ncg777.Music.Rhythm;

public class HighDispersion implements StandardAndGuavaPredicate<Rhythm> {
  private CombinationDispersion cd = new CombinationDispersion();
  
  @Override
  public boolean apply(@Nonnull Rhythm arg0) {
    return cd.apply(arg0).quantize(0.65);
  }

}
