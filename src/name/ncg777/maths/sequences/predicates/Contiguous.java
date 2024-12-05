package name.ncg777.maths.sequences.predicates;

import name.ncg777.computing.Functional.StandardAndGuavaPredicate;
import name.ncg777.maths.sequences.Sequence;

public class Contiguous implements StandardAndGuavaPredicate<Sequence>{

  private int value;
  public Contiguous(int value){
    this.value = value;
  }
  @SuppressWarnings("null")
  @Override
  public boolean apply(Sequence input) {
    Sequence order = new Sequence();
    
    for(int i=0;i<input.size();i++){
      if(input.get(i) == value){
        order.add(i);
        if(order.size()>1 && order.get(order.size()-2)+1!=order.get(order.size()-1)){
          return false;
        }
      }
    }
    
    return order.size()!=0;
  }

}
