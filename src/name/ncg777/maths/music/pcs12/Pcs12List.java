package name.ncg777.maths.music.pcs12;

import java.util.ArrayList;
import java.util.function.Function;

import com.google.common.base.Joiner;

import name.ncg777.maths.Numbers;

public class Pcs12List extends ArrayList<Pcs12> {

  private static final long serialVersionUID = 1L;

  public Pcs12List transpose(int n) {
    Pcs12List o = new Pcs12List();
    for (int i = 0; i < this.size(); i++) {
      o.add(this.get(i).transpose(n));
    }
    return o;
  }
  
  public static Pcs12List parse(String s) {
    var ss = s.split("\\s+");
    Pcs12List o = new Pcs12List();
    for(String _s : ss) {
      o.add(Pcs12.parse(_s));
    }
    return o;
  }
  public static Pcs12List parseForte(String s) {
    var ss = s.split("\\s+");
    Pcs12List o = new Pcs12List();
    for(String _s : ss) {
      o.add(Pcs12.parseForte(_s));
    }
    return o;
  }
  public String toString(Function<Pcs12,String> printer) {
    return Joiner.on(" ").join(this.stream().map((c) -> printer.apply(c)).toList());
  }
  @Override
  public String toString() {
    return toString((c) -> c.toString());
  }
  public static Pcs12List merge(ArrayList<Pcs12List> s) {
    int n = 1;
    
    for(var _s:s) {n = (int)Numbers.lcm(n,_s.size());}
    Pcs12List o = new Pcs12List();
    for(int i=0;i<n;i++) {
      Pcs12 m = Pcs12.empty();
      for(int j=0;j<s.size();j++) {
        m = m.combineWith(s.get(j).get(i%s.get(j).size()));
      }
      o.add(m);
    }
    return o;
    
  }
}
