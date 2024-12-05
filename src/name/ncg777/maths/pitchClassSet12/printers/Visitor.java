package name.ncg777.maths.pitchClassSet12.printers;

import java.util.List;
import java.util.TreeSet;

import com.google.common.base.Function;

import name.ncg777.maths.pitchClassSet12.PitchClassSet12;
import name.ncg777.maths.pitchClassSet12.PitchClassSet12List;

public class Visitor implements Function<List<PitchClassSet12>, Void> {

  TreeSet<PitchClassSet12List> t;

  public Visitor() {
    t = new TreeSet<PitchClassSet12List>();
  }

  @Override
  public Void apply(List<PitchClassSet12> input) {
    PitchClassSet12List cs = new PitchClassSet12List();
    cs.addAll(input);
    t.add(cs);
    return null;
  }

  public TreeSet<PitchClassSet12List> getSet() {
    return t;
  }

}
