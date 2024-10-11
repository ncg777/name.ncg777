package name.NicolasCoutureGrenier.Music;

import java.util.ArrayList;
import java.util.function.Function;

import com.google.common.base.Joiner;

import name.NicolasCoutureGrenier.Maths.Numbers;

public class PCS12Sequence extends ArrayList<PCS12> {

  private static final long serialVersionUID = 1L;

  public PCS12Sequence transpose(int n) {
    PCS12Sequence o = new PCS12Sequence();
    for (int i = 0; i < this.size(); i++) {
      o.add(this.get(i).transpose(n));
    }
    return o;
  }
  
  public static PCS12Sequence parse(String s) {
    var ss = s.split("\\s+");
    PCS12Sequence o = new PCS12Sequence();
    for(String _s : ss) {
      o.add(PCS12.parse(_s));
    }
    return o;
  }
  public static PCS12Sequence parseForte(String s) {
    var ss = s.split("\\s+");
    PCS12Sequence o = new PCS12Sequence();
    for(String _s : ss) {
      o.add(PCS12.parseForte(_s));
    }
    return o;
  }
  public String toString(Function<PCS12,String> printer) {
    return Joiner.on(" ").join(this.stream().map((c) -> printer.apply(c)).toList());
  }
  @Override
  public String toString() {
    return toString((c) -> c.toString());
  }
  public static PCS12Sequence merge(ArrayList<PCS12Sequence> s) {
    int n = 1;
    
    for(var _s:s) {n = (int)Numbers.lcm(n,_s.size());}
    PCS12Sequence o = new PCS12Sequence();
    for(int i=0;i<n;i++) {
      PCS12 m = PCS12.empty();
      for(int j=0;j<s.size();j++) {
        m = m.combineWith(s.get(j).get(i%s.get(j).size()));
      }
      o.add(m);
    }
    return o;
    
  }
}
