package name.ncg777.musical;

import java.util.ArrayList;
import java.util.function.Function;

import com.google.common.base.Joiner;

import name.ncg777.mathematics.Numbers;

public class pitchClassSet12Sequence extends ArrayList<pitchClassSet12> {

  private static final long serialVersionUID = 1L;

  public pitchClassSet12Sequence transpose(int n) {
    pitchClassSet12Sequence o = new pitchClassSet12Sequence();
    for (int i = 0; i < this.size(); i++) {
      o.add(this.get(i).transpose(n));
    }
    return o;
  }
  
  public static pitchClassSet12Sequence parse(String s) {
    var ss = s.split("\\s+");
    pitchClassSet12Sequence o = new pitchClassSet12Sequence();
    for(String _s : ss) {
      o.add(pitchClassSet12.parse(_s));
    }
    return o;
  }
  public static pitchClassSet12Sequence parseForte(String s) {
    var ss = s.split("\\s+");
    pitchClassSet12Sequence o = new pitchClassSet12Sequence();
    for(String _s : ss) {
      o.add(pitchClassSet12.parseForte(_s));
    }
    return o;
  }
  public String toString(Function<pitchClassSet12,String> printer) {
    return Joiner.on(" ").join(this.stream().map((c) -> printer.apply(c)).toList());
  }
  @Override
  public String toString() {
    return toString((c) -> c.toString());
  }
  public static pitchClassSet12Sequence merge(ArrayList<pitchClassSet12Sequence> s) {
    int n = 1;
    
    for(var _s:s) {n = (int)Numbers.lcm(n,_s.size());}
    pitchClassSet12Sequence o = new pitchClassSet12Sequence();
    for(int i=0;i<n;i++) {
      pitchClassSet12 m = pitchClassSet12.empty();
      for(int j=0;j<s.size();j++) {
        m = m.combineWith(s.get(j).get(i%s.get(j).size()));
      }
      o.add(m);
    }
    return o;
    
  }
}
