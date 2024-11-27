package name.ncg777.mathematics.relations;

import java.util.function.BiPredicate;

import name.ncg777.mathematics.objects.Sequence;

public class LessThan implements BiPredicate<Sequence, Sequence> {

  @Override
  public boolean test(Sequence a, Sequence b) {
    if(a.size()!=b.size() || a.size()==0) return false;
    
    int n = a.size();
    
    for(int i=0;i<n;i++) {
      if(b.get(i) < a.get(i)) return false;
    }
    return !a.equals(b);
  }

}
