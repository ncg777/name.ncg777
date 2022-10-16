package name.NicolasCoutureGrenier.Music.PCS12ListPrinters;

import java.util.List;
import java.util.TreeSet;

import name.NicolasCoutureGrenier.Music.PCS12;
import name.NicolasCoutureGrenier.Music.PCS12Sequence;

import com.google.common.base.Function;

public class Visitor implements Function<List<PCS12>, Void> {

  TreeSet<PCS12Sequence> t;

  public Visitor() {
    t = new TreeSet<PCS12Sequence>();
  }

  @Override
  public Void apply(List<PCS12> input) {
    PCS12Sequence cs = new PCS12Sequence();
    cs.addAll(input);
    t.add(cs);
    return null;
  }

  public TreeSet<PCS12Sequence> getSet() {
    return t;
  }

}
