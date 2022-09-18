package name.ncg.Maths.Predicates;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import name.ncg.Maths.DataStructures.ComparableSet;
import name.ncg.Maths.DataStructures.Sequence;

public class ContiguousDistinctValuesExceptZero implements Predicate<Sequence>{

  @Override
  public boolean apply(Sequence input) {
    ComparableSet<Integer> s = input.distinct();
    Predicate<Sequence> p = new Predicate<Sequence>(){
      @Override
      public boolean apply(Sequence input) {
        return true;
      }};
      
    for(Integer v : s){
      if(v!=0){
          p = Predicates.and(p,new Contiguous(v));
      }
    }
    return p.apply(input);
  }

}
