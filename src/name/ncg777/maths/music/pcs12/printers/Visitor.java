package name.ncg777.maths.music.pcs12.printers;

import java.util.List;
import java.util.TreeSet;

import com.google.common.base.Function;

import name.ncg777.maths.music.pcs12.Pcs12;
import name.ncg777.maths.music.pcs12.Pcs12List;

public class Visitor implements Function<List<Pcs12>, Void> {

  TreeSet<Pcs12List> t;

  public Visitor() {
    t = new TreeSet<Pcs12List>();
  }

  @Override
  public Void apply(List<Pcs12> input) {
    Pcs12List cs = new Pcs12List();
    cs.addAll(input);
    t.add(cs);
    return null;
  }

  public TreeSet<Pcs12List> getSet() {
    return t;
  }

}
