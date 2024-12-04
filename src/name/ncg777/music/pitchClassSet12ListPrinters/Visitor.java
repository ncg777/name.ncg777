package name.ncg777.music.pitchClassSet12ListPrinters;

import java.util.List;
import java.util.TreeSet;

import com.google.common.base.Function;

import name.ncg777.music.PitchClassSet12;
import name.ncg777.music.PitchClassSet12Sequence;

public class Visitor implements Function<List<PitchClassSet12>, Void> {

  TreeSet<PitchClassSet12Sequence> t;

  public Visitor() {
    t = new TreeSet<PitchClassSet12Sequence>();
  }

  @Override
  public Void apply(List<PitchClassSet12> input) {
    PitchClassSet12Sequence cs = new PitchClassSet12Sequence();
    cs.addAll(input);
    t.add(cs);
    return null;
  }

  public TreeSet<PitchClassSet12Sequence> getSet() {
    return t;
  }

}
