package name.ncg777.music;

import java.util.ArrayList;
import java.util.function.Function;

import com.google.common.base.Joiner;

import name.ncg777.maths.Numbers;

public class PitchClassSet12Sequence extends ArrayList<PitchClassSet12> {

  private static final long serialVersionUID = 1L;

  public PitchClassSet12Sequence transpose(int n) {
    PitchClassSet12Sequence o = new PitchClassSet12Sequence();
    for (int i = 0; i < this.size(); i++) {
      o.add(this.get(i).transpose(n));
    }
    return o;
  }
  
  public static PitchClassSet12Sequence parse(String s) {
    var ss = s.split("\\s+");
    PitchClassSet12Sequence o = new PitchClassSet12Sequence();
    for(String _s : ss) {
      o.add(PitchClassSet12.parse(_s));
    }
    return o;
  }
  public static PitchClassSet12Sequence parseForte(String s) {
    var ss = s.split("\\s+");
    PitchClassSet12Sequence o = new PitchClassSet12Sequence();
    for(String _s : ss) {
      o.add(PitchClassSet12.parseForte(_s));
    }
    return o;
  }
  public String toString(Function<PitchClassSet12,String> printer) {
    return Joiner.on(" ").join(this.stream().map((c) -> printer.apply(c)).toList());
  }
  @Override
  public String toString() {
    return toString((c) -> c.toString());
  }
  public static PitchClassSet12Sequence merge(ArrayList<PitchClassSet12Sequence> s) {
    int n = 1;
    
    for(var _s:s) {n = (int)Numbers.lcm(n,_s.size());}
    PitchClassSet12Sequence o = new PitchClassSet12Sequence();
    for(int i=0;i<n;i++) {
      PitchClassSet12 m = PitchClassSet12.empty();
      for(int j=0;j<s.size();j++) {
        m = m.combineWith(s.get(j).get(i%s.get(j).size()));
      }
      o.add(m);
    }
    return o;
    
  }
}
