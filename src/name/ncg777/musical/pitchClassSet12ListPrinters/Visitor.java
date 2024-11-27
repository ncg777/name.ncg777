package name.ncg777.musical.pitchClassSet12ListPrinters;

import java.util.List;
import java.util.TreeSet;

import com.google.common.base.Function;

import name.ncg777.musical.pitchClassSet12;
import name.ncg777.musical.pitchClassSet12Sequence;

public class Visitor implements Function<List<pitchClassSet12>, Void> {

  TreeSet<pitchClassSet12Sequence> t;

  public Visitor() {
    t = new TreeSet<pitchClassSet12Sequence>();
  }

  @Override
  public Void apply(List<pitchClassSet12> input) {
    pitchClassSet12Sequence cs = new pitchClassSet12Sequence();
    cs.addAll(input);
    t.add(cs);
    return null;
  }

  public TreeSet<pitchClassSet12Sequence> getSet() {
    return t;
  }

}
