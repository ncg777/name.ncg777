package name.ncg777.Maths.Predicates;

import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import name.ncg777.CS.Functional.StandardAndGuavaPredicate;
import name.ncg777.Maths.Objects.Sequence;

public class ContiguousDistinctValuesExceptZero implements StandardAndGuavaPredicate<Sequence>{

  @SuppressWarnings("null")
  @Override
  public boolean apply(Sequence input) {
    Set<Integer> s = input.distinct();
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
